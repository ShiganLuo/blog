package com.baofeng.blog.service.impl;

import com.baofeng.blog.exception.DuplicateUserException;
import com.baofeng.blog.mapper.UserMapper;
import com.baofeng.blog.service.UserService;
import com.baofeng.blog.entity.User;
import com.baofeng.blog.exception.AuthException;
import com.baofeng.blog.vo.admin.AdminUserAuthVO.*;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
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
    public User loginUser(LoginRequest loginDTO) {
        User user = userMapper.selectByUsernameOrEmail(loginDTO.username());
        if (user == null) {
            throw new AuthException(400,"用户不存在");
        }
        else if (!passwordEncoder.matches(loginDTO.password(), user.getPassword())) {
            userMapper.incrementLoginAttempts(user.getId());
            throw new AuthException(400,"密码错误");
        }
        else if (user.getStatus() == User.Status.BANNED) {
            throw new AuthException(403,"账户已被锁定");
        }
        else {
            //什么都不做
        }

        userMapper.updateLoginInfo(user.getId());
        return user;
    }
    
    @Override
    public User getUserByUsername(String username) {
        // 此处直接复用 UserMapper 中 selectByUsernameOrEmail 方法
        return userMapper.selectByUsernameOrEmail(username);
    }
    @Override
    public User getUserInfoById(Long id){
        return userMapper.selectUserById(id);
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