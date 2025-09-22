package com.baofeng.blog.service.impl;

import com.baofeng.blog.exception.DuplicateUserException;
import com.baofeng.blog.mapper.UserMapper;
import com.baofeng.blog.service.CustomUserDetailsService;
import com.baofeng.blog.service.UserService;
import com.baofeng.blog.util.ResultCode;
import com.baofeng.blog.config.JwtPropertiesConfig;
import com.baofeng.blog.entity.User;
import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.admin.AdminLoginResponseVO;
import com.baofeng.blog.vo.admin.AdminUserAuthVO.*;
import com.baofeng.blog.vo.common.User.LoginRequest;
import com.baofeng.blog.vo.front.FrontUserVO.FrontLoginResponseVO;
import com.baofeng.blog.util.JwtTokenProvider;
import com.baofeng.blog.util.LoginType;
import com.baofeng.blog.mapper.RoleMapper;
import com.baofeng.blog.entity.Role;
import com.baofeng.blog.enums.RoleType;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(UserMapper userMapper,
                            RoleMapper roleMapper,
                            BCryptPasswordEncoder passwordEncoder,
                            JwtTokenProvider jwtTokenProvider, 
                            JwtPropertiesConfig jwtProperties) {
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userMapper = userMapper;
        this.accessTokenExpiration = jwtProperties.getAccessTokenExpiration();
        this.refreshTokenExpiration = jwtProperties.getRefreshTokenExpiration();
        this.roleMapper = roleMapper;
    }

    @Override
    @Transactional
    public ApiResponse<String> registerUser(RegisterRequest registerDTO) {
        // 检查用户名和邮箱唯一性
        checkUserUniqueness(registerDTO.username());
        
        User newUser = new User();
        newUser.setUsername(registerDTO.username());
        newUser.setPassword(passwordEncoder.encode(registerDTO.password()));
        newUser.setStatus(User.Status.ACTIVE);
        newUser.setNickName(registerDTO.username());
        int rowUpdated1 = userMapper.insertUser(newUser);

        if (rowUpdated1 == 0) {
            logger.error("用户插入数据库失败");
            return ApiResponse.error(ResultCode.SERVER_ERROR,"用户创建失败");
        }
        // 更新roles表
        Role role = roleMapper.selectRolesByRoleName(RoleType.USER.getName());
        if (role == null) {
            logger.info("USER不存在于roles表,创建USER角色");
            role = new Role();
            role.setRoleName(RoleType.USER.getName()); // 默认分配USER权限
            role.setRoleDesc(RoleType.USER.getDescription());
            int rowUpdated2 = roleMapper.insertRole(role);
            if (rowUpdated2 == 0) {
                logger.error("USER角色创建失败");
                return ApiResponse.error(ResultCode.SERVER_ERROR,"用户创建失败");
            }
        }

        //更新user_roles表
        int rowUpdated3 = roleMapper.insertUserRole(newUser.getId(), role.getId());

        return rowUpdated3 > 0 
            ? ApiResponse.success("用户创建成功")
            : ApiResponse.error(ResultCode.SERVER_ERROR,"用户创建失败");
    }

    private void checkUserUniqueness(String username) {
        if (userMapper.selectByUsernameOrEmail(username) != null) {
            throw new DuplicateUserException("用户名已存在");
        }
    }

    @Override
    public ApiResponse<FrontLoginResponseVO> loginUserFront(LoginRequest loginDTO) {
        return login(loginDTO, LoginType.FRONT);
    }

    public ApiResponse<AdminLoginResponseVO> loginUserAdmin(LoginRequest loginDTO) {
        return login(loginDTO, LoginType.ADMIN);
    }

    @SuppressWarnings("unchecked")
    private <T> ApiResponse<T> login(LoginRequest loginDTO, LoginType type) {
        User user = userMapper.selectByUsernameOrEmail(loginDTO.username());

        if (user == null) {
            return (ApiResponse<T>) ApiResponse.error(ResultCode.NOT_FOUND, "用户不存在");
        } else if (!passwordEncoder.matches(loginDTO.password(), user.getPassword())) {
            userMapper.incrementLoginAttempts(user.getId());
            return (ApiResponse<T>) ApiResponse.error(ResultCode.PARAM_ERROR, "密码错误");
        } else if (user.getStatus() == User.Status.BANNED) {
            return (ApiResponse<T>) ApiResponse.error(ResultCode.UNAUTHORIZED, "账户被锁定");
        }

        userMapper.updateLoginInfo(user.getId());

        String accessToken = jwtTokenProvider.generateToken(user, accessTokenExpiration, false);
        String refreshToken = jwtTokenProvider.generateToken(user, refreshTokenExpiration, true);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expires = now.plus(1, ChronoUnit.HOURS);

        List<String> roles = roleMapper.selectRolesByUserId(user.getId()).stream()
            .map(Role::getRoleName)
            .collect(Collectors.toList());
        if (type == LoginType.FRONT) {
            FrontLoginResponseVO.User userInfo = new FrontLoginResponseVO.User(
                user.getId(),
                user.getAvatarUrl(),
                user.getUsername(),
                user.getNickName(),
                roles
            );
            FrontLoginResponseVO response = new FrontLoginResponseVO(accessToken, refreshToken, expires, userInfo);
            return (ApiResponse<T>) ApiResponse.success(response);
        } else {
            AdminLoginResponseVO.User userInfo = new AdminLoginResponseVO.User(
                user.getId(),
                user.getAvatarUrl(),
                user.getUsername(),
                user.getNickName(),
                roles
            );
            AdminLoginResponseVO response = new AdminLoginResponseVO(accessToken, refreshToken, expires, userInfo);
            return (ApiResponse<T>) ApiResponse.success(response);
        }
    }
    
    @Override
    public User getUserByUsername(String username) {
        // 此处直接复用 UserMapper 中 selectByUsernameOrEmail 方法
        return userMapper.selectByUsernameOrEmail(username);
    }
    @Override
    public ApiResponse<User> getUserInfoById(Long id){
        User user = userMapper.selectUserById(id);
        return user != null
            ? ApiResponse.success(user)
            : ApiResponse.error(ResultCode.PARAM_ERROR,"用户不存在");
    }

    @Override
    public ApiResponse<String> updateUserRole(UpdateUserRoleRequest updateUserRoleRequest) {
        Long userId = updateUserRoleRequest.userId();
        List<String> roles = updateUserRoleRequest.roles();
        List<String> validRoles = List.of(RoleType.USER.getName(), RoleType.ADMIN.getName());
        // 检查用户是否存在
        User user = userMapper.selectUserById(userId);
        if (user == null) {
            return ApiResponse.error(ResultCode.NOT_FOUND, "用户不存在");
        }
        // 检查角色是否有效
        logger.info("有效用户角色: {}", validRoles);
        logger.info("请求更新角色: {}", roles);
        for (String roleName : roles) {
            if (!validRoles.contains(roleName)) {
                return ApiResponse.error(ResultCode.PARAM_ERROR, "无效的角色: " + roleName);
            }
        }
        // 删除用户现有角色
        roleMapper.deleteUserRolesByUserId(userId);
        // 为用户分配新角色
        for (String roleName : roles) {
            Role role = roleMapper.selectRolesByRoleName(roleName);
            if (role == null) {
                // 如果角色不存在，创建新角色
                role = new Role();
                role.setRoleName(RoleType.valueOf(roleName).getName());
                role.setRoleDesc(RoleType.valueOf(roleName).getDescription());
                int rowUpdated = roleMapper.insertRole(role);
                if (rowUpdated == 0) {
                    logger.error("角色创建失败: " + roleName);
                }
            }
            int rowUpdated = roleMapper.insertUserRole(userId, role.getId());
            if (rowUpdated == 0) {
                logger.error("为用户分配角色失败: " + roleName);
            }
        }
        return ApiResponse.success("角色更新成功");
    }

    @Override
    public ApiResponse<userPageResponse> getUserList(userPageRequest param) {
        int offset = (param.getCurrent() - 1) * param.getSize();
        int pageSize = param.getSize();
        // 先查询 User 列表
        List<User> userslist = userMapper.selectByPage(offset, pageSize);
        int total = userslist.size();
        // 手动转换为 UserVO
        List<userPageVO> voList = userslist.stream()
            .map(user -> {
                userPageVO vo = new userPageVO();
                vo.setUsername(user.getUsername());
                vo.setNickName(user.getNickName());
                vo.setAvatarUrl(user.getAvatarUrl());
                vo.setCreatedAt(user.getCreatedAt());
                vo.setUpdatedAt(user.getUpdatedAt());
                return vo;
            })
            .collect(Collectors.toList());
        
        userPageResponse result = new userPageResponse();
        result.setList(voList);
        result.setTotal(total);
        return ApiResponse.success(result);
    }
    @Override
    public ApiResponse<String> updatePassword(String username,String newPassword){

        int result = userMapper.updatePassword(username, passwordEncoder.encode(newPassword));
        return result > 0
            ? ApiResponse.success()
            : ApiResponse.error(ResultCode.SERVER_ERROR,"密码更新失败");
    }

    @Override
    public ApiResponse<refreshTokenResponse> refreshToken(String rawToken) {
        String refreshToken = rawToken.replaceAll("^\"|\"$", "");
        boolean isTokenExpired = jwtTokenProvider.isTokenExpired(refreshToken);
        boolean isRefreshToken = jwtTokenProvider.isRefreshToken(refreshToken);
        if ( !isTokenExpired && !isRefreshToken ){
            String username = jwtTokenProvider.getUserNameFromToken(refreshToken);
            User user = userMapper.selectByUsernameOrEmail(username);
            String accessToken = jwtTokenProvider.generateToken(user, accessTokenExpiration,false);
            refreshTokenResponse response = new refreshTokenResponse();
            response.setAccessToken(accessToken);
            response.setRefreshToken(refreshToken);
            response.setExpires(LocalDateTime.now().plus(1, ChronoUnit.HOURS));
            return ApiResponse.success(response);
        } else {
            return !isRefreshToken
            ? ApiResponse.error(ResultCode.PARAM_ERROR,"token类型错误")
            : ApiResponse.error(ResultCode.PARAM_ERROR,"token过期");
            // return ApiResponse.error(ResultCode.PARAM_ERROR,"refreshToken验证失败");
        }
    }

    @Override
    public ApiResponse<User> getUserInfoByToken(String BearerToken) {
        String token = BearerToken.substring(7); // 去除 "Bearer " 前缀获取真正的 token
        // 验证 token 并获取用户名
        String username = jwtTokenProvider.getUserNameFromToken(token);

        if (username == null) {
            return ApiResponse.error(ResultCode.PARAM_ERROR, "无效的 token");
        }
        // 根据用户名查询数据库返回用户信息
        User user = userMapper.selectByUsernameOrEmail(username);
        if (user == null) {
            return ApiResponse.error(ResultCode.PARAM_ERROR, "未找到用户信息");
        }
        return ApiResponse.success(user);
    }
} 