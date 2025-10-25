package com.baofeng.blog.service.impl;

import com.baofeng.blog.exception.DuplicateUserException;
import com.baofeng.blog.mapper.UserMapper;
import com.baofeng.blog.service.UserService;
import com.baofeng.blog.config.JwtPropertiesConfig;
import com.baofeng.blog.entity.User;
import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.admin.AdminLoginResponseVO;
import com.baofeng.blog.vo.admin.AdminUserAuthVO.*;
import com.baofeng.blog.vo.common.User.LoginRequest;
import com.baofeng.blog.vo.common.User.UserInfoResponse;
import com.baofeng.blog.vo.front.FrontUserVO.FrontLoginResponseVO;
import com.baofeng.blog.util.JwtTokenProvider;
import com.baofeng.blog.mapper.RoleMapper;
import com.baofeng.blog.entity.Role;
import com.baofeng.blog.enums.GenderEnum;
import com.baofeng.blog.enums.ResultCodeEnum;
import com.baofeng.blog.enums.RoleTypeEnum;
import com.baofeng.blog.enums.UserStatusEnum;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        newUser.setStatus(UserStatusEnum.ACTIVE.name());
        newUser.setNickName(registerDTO.username());
        int rowUpdated1 = userMapper.insertUser(newUser);

        if (rowUpdated1 == 0) {
            logger.error("用户插入数据库失败");
            return ApiResponse.error(ResultCodeEnum.INTERNEL_SERVER_ERROR,"用户创建失败");
        }
        // 更新roles表
        Role role = roleMapper.selectRolesByRoleName(RoleTypeEnum.USER.getRole());
        if (role == null) {
            logger.info("USER不存在于roles表,创建USER角色");
            role = new Role();
            role.setRoleName(RoleTypeEnum.USER.getRole()); // 默认分配USER权限
            role.setRoleDesc(RoleTypeEnum.USER.getDescription());
            int rowUpdated2 = roleMapper.insertRole(role);
            if (rowUpdated2 == 0) {
                logger.error("USER角色创建失败");
                return ApiResponse.error(ResultCodeEnum.INTERNEL_SERVER_ERROR,"用户创建失败");
            }
        }

        //更新user_roles表
        int rowUpdated3 = roleMapper.insertUserRole(newUser.getId(), role.getId());

        return rowUpdated3 > 0 
            ? ApiResponse.success("用户创建成功")
            : ApiResponse.error(ResultCodeEnum.INTERNEL_SERVER_ERROR,"用户创建失败");
    }

    private void checkUserUniqueness(String username) {
        if (userMapper.selectByUsernameOrEmail(username) != null) {
            throw new DuplicateUserException("用户名已存在");
        }
    }

    @Override
    public ApiResponse<FrontLoginResponseVO> loginUserFront(LoginRequest loginDTO) {
        return login(loginDTO, FrontLoginResponseVO.class);
    }

    @Override
    public ApiResponse<AdminLoginResponseVO> loginUserAdmin(LoginRequest loginDTO) {
        return login(loginDTO, AdminLoginResponseVO.class);
    }

    private <T> ApiResponse<T> login(LoginRequest loginDTO, Class<T> clazz) {
        // 查询用户
        User user = userMapper.selectByUsernameOrEmail(loginDTO.username());
        if (user == null) {
            return ApiResponse.error(ResultCodeEnum.NOT_FOUND, "用户不存在");
        }
        if (!passwordEncoder.matches(loginDTO.password(), user.getPassword())) {
            userMapper.incrementLoginAttempts(user.getId());
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST, "密码错误");
        }
        if (user.getStatus() == UserStatusEnum.BANNED.name()) {
            return ApiResponse.error(ResultCodeEnum.UNAUTHORIZED, "账户被锁定");
        }

        // 更新登录信息
        userMapper.updateLoginInfo(user.getId());

        // 生成 token
        String accessToken = jwtTokenProvider.generateToken(user, accessTokenExpiration, false);
        String refreshToken = jwtTokenProvider.generateToken(user, refreshTokenExpiration, true);

        // 获取角色
        List<String> roles = roleMapper.selectRolesByUserId(user.getId())
            .stream()
            .map(Role::getRoleName)
            .collect(Collectors.toList());

        Object response;

        if (clazz == FrontLoginResponseVO.class) {
            FrontLoginResponseVO.User userInfo = new FrontLoginResponseVO.User(
                user.getId(),
                user.getAvatarUrl(),
                user.getUsername(),
                user.getNickName(),
                roles
            );
            response = new FrontLoginResponseVO(accessToken, refreshToken, userInfo);
        } else if (clazz == AdminLoginResponseVO.class) {
            AdminLoginResponseVO.User userInfo = new AdminLoginResponseVO.User(
                user.getId(),
                user.getAvatarUrl(),
                user.getUsername(),
                user.getNickName(),
                roles
            );
            response = new AdminLoginResponseVO(accessToken, refreshToken, userInfo);
        } else {
            logger.error("不支持的登录类型: {}", clazz.getName());
            throw new IllegalArgumentException("不支持的登录类型");
        }

        // 类型安全转换
        return ApiResponse.success(clazz.cast(response));
    }

    
    @Override
    public User getUserByUsername(String username) {
        // 此处直接复用 UserMapper 中 selectByUsernameOrEmail 方法
        return userMapper.selectByUsernameOrEmail(username);
    }
    @Override
    public ApiResponse<UserInfoResponse> getUserInfoById(Long id){
        User user = userMapper.selectUserById(id);
        if (user ==  null) {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST,"用户不存在");
        }
        List<String> roles = roleMapper.selectRolesByUserId(user.getId())
            .stream()
            .map(Role::getRoleName)
            .collect(Collectors.toList());
        UserInfoResponse userInfoResponse = buildUserInfoResponse(user, roles);
        return ApiResponse.success(userInfoResponse);
    }

    @Override
    public ApiResponse<String> updateUserRole(UpdateUserRoleRequest updateUserRoleRequest) {
        Long userId = updateUserRoleRequest.userId();
        List<String> roles = updateUserRoleRequest.roles();
        List<String> validRoles = List.of(RoleTypeEnum.USER.getRole(), RoleTypeEnum.ADMIN.getRole());
        // 检查用户是否存在
        User user = userMapper.selectUserById(userId);
        if (user == null) {
            return ApiResponse.error(ResultCodeEnum.NOT_FOUND, "用户不存在");
        }

        for (String roleName : roles) {
            if (!validRoles.contains(roleName)) {
                return ApiResponse.error(ResultCodeEnum.BAD_REQUEST, "无效的角色: " + roleName);
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
                role.setRoleName(RoleTypeEnum.valueOf(roleName).getRole());
                role.setRoleDesc(RoleTypeEnum.valueOf(roleName).getDescription());
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
            : ApiResponse.error(ResultCodeEnum.INTERNEL_SERVER_ERROR,"密码更新失败");
    }

    @Override
    public ApiResponse<String> refreshToken(String rawToken) {
        if (rawToken == null) {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST,"refreshToken不能为空");
        }
        String refreshToken = rawToken.replaceAll("^\"|\"$", "");
        boolean isTokenExpired = jwtTokenProvider.isTokenExpired(refreshToken);
        boolean isRefreshToken = jwtTokenProvider.isRefreshToken(refreshToken);
        if ( !isTokenExpired && isRefreshToken ){
            String username = jwtTokenProvider.getUserNameFromToken(refreshToken);
            User user = userMapper.selectByUsernameOrEmail(username);
            String accessToken = jwtTokenProvider.generateToken(user, accessTokenExpiration,false);
            return ApiResponse.success(accessToken);
        } else {
            return !isRefreshToken
            ? ApiResponse.error(ResultCodeEnum.BAD_REQUEST,"token类型错误")
            : ApiResponse.error(ResultCodeEnum.BAD_REQUEST,"token过期");
            // return ApiResponse.error(ResultCode.BAD_REQUEST,"refreshToken验证失败");
        }
    }

    @Override
    public ApiResponse<UserInfoResponse> getUserInfoByToken(String BearerToken) {
        String token = BearerToken.substring(7); // 去除 "Bearer " 前缀获取真正的 token
        // 验证 token 并获取用户名
        String username = jwtTokenProvider.getUserNameFromToken(token);

        if (username == null) {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST, "无效的 token");
        }
        // 根据用户名查询数据库返回用户信息
        User user = userMapper.selectByUsernameOrEmail(username);
        if (user == null) {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST, "未找到用户信息");
        }
        List<String> roles = roleMapper.selectRolesByUserId(user.getId())
            .stream()
            .map(Role::getRoleName)
            .collect(Collectors.toList());
        UserInfoResponse response = buildUserInfoResponse(user,roles);
        
        return ApiResponse.success(response);
    }

    public static UserInfoResponse buildUserInfoResponse(User user, List<String> roles) {

        GenderEnum genderEnum = GenderEnum.fromCode(user.getGender());
        return UserInfoResponse.builder()
        .id(user.getId())
        .username(user.getUsername())
        .email(user.getEmail())
        .avatarUrl(user.getAvatarUrl())
        .bio(user.getBio())
        .nickName(user.getNickName())
        .phoneNumber(user.getPhoneNumber())
        .gender(genderEnum.getGender())
        .status(user.getStatus())
        .createdAt(user.getCreatedAt())
        .updatedAt(user.getUpdatedAt())
        .lastLogin(user.getLastLogin())
        .loginAttempts(user.getLoginAttempts())
        .isEmailVerified(user.getIsEmailVerified())
        .isActive(user.getIsActive())
        .roles(roles)
        .password(null)
        .build();
    }

    @Override
    public ApiResponse<String> updateUserInfo(UpdateUserInfo updateUserInfo) {
        
        if (updateUserInfo.id() == null) {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST,"用户id不能为空");
        }
        User isUser = userMapper.selectUserById(updateUserInfo.id());
        if (isUser == null) {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST,"用户不存在");
        }
        User user = new User();
        Integer gender = updateUserInfo.gender();
        
        if (GenderEnum.isCodeExit(gender)) {
            user.setGender(updateUserInfo.gender());
        } else {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST,"不支持的性别");
        }
        user.setId(updateUserInfo.id());
        user.setUsername(updateUserInfo.username());
        user.setNickName(updateUserInfo.nickName());
        user.setEmail(updateUserInfo.email());
        user.setPhoneNumber(updateUserInfo.phoneNumber());
        int rowUpdated = userMapper.updateUserSelective(user);
        return rowUpdated > 0
            ? ApiResponse.success("用户信息更新成功")
            : ApiResponse.error(ResultCodeEnum.INTERNEL_SERVER_ERROR,"用户信息更新失败");

    }
} 