package com.baofeng.blog.service.impl;

import com.baofeng.blog.exception.DuplicateUserException;
import com.baofeng.blog.mapper.UserMapper;
import com.baofeng.blog.service.UserService;
import com.baofeng.blog.util.ResultCode;
import com.baofeng.blog.entity.User;
import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.admin.AdminLoginResponseVO;
import com.baofeng.blog.vo.admin.AdminUserAuthVO.*;
import com.baofeng.blog.vo.common.User.LoginRequest;
import com.baofeng.blog.vo.front.FrontUserVO.FrontLoginResponseVO;
import com.baofeng.blog.util.JwtTokenProvider;
import com.baofeng.blog.util.LoginType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public UserServiceImpl(BCryptPasswordEncoder passwordEncoder,JwtTokenProvider jwtTokenProvider) {
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    @Transactional
    public User registerUser(RegisterRequest registerDTO) {
        // 检查用户名和邮箱唯一性
        checkUserUniqueness(registerDTO.username());
        
        User newUser = new User();
        newUser.setUsername(registerDTO.username());
        newUser.setPassword(passwordEncoder.encode(registerDTO.password()));
        newUser.setRole(User.Role.USER);
        newUser.setStatus(User.Status.ACTIVE);
        
        userMapper.insertUser(newUser);
        return newUser;
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

        String accessToken = jwtTokenProvider.generateToken(user, 3600000, false);
        String refreshToken = jwtTokenProvider.generateToken(user, 1209600000, true);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expires = now.plus(1, ChronoUnit.HOURS);

        if (type == LoginType.FRONT) {
            FrontLoginResponseVO.User userInfo = new FrontLoginResponseVO.User(
                user.getId(),
                user.getAvatarUrl(),
                user.getUsername(),
                user.getNickName(),
                user.getRole().name()
            );
            FrontLoginResponseVO response = new FrontLoginResponseVO(accessToken, refreshToken, expires, userInfo);
            return (ApiResponse<T>) ApiResponse.success(response);
        } else {
            AdminLoginResponseVO.User userInfo = new AdminLoginResponseVO.User(
                user.getAvatarUrl(),
                user.getUsername(),
                user.getNickName(),
                user.getRole().name()
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
        return ApiResponse.success(user);
    }

    @Override
    public boolean updateUserRole(Long id, String role) {
        User user = userMapper.selectUserById(id);
        if (user != null) {
            user.setRole(User.Role.valueOf(role.toUpperCase()));
            userMapper.updateUserSelective(user);
            return true;
        }
        return false;
    }

    @Override
    public userPageResponse getUserList(userPageRequest param) {
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
        return result;
    }
    @Override
    public boolean updatePassword(String username,String newPassword){

        int result = userMapper.updatePassword(username, passwordEncoder.encode(newPassword));
        return result > 0;
    }
} 