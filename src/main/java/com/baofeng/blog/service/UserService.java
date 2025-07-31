package com.baofeng.blog.service;

import com.baofeng.blog.entity.User;
import com.baofeng.blog.vo.admin.AdminUserAuthVO.*;
import com.baofeng.blog.vo.common.User.LoginRequest;



public interface UserService {
    User registerUser(RegisterRequest registerDTO);
    User loginUser(LoginRequest loginDTO);
    User getUserByUsername(String username);
    /**
     * 根据id获取用户信息
     * @param id
     * @return
     */
    User getUserInfoById(Long id);
    
    boolean updateUserRole(Long id, String role);
    userPageResponse getUserList(userPageRequest param);
    boolean updatePassword(String username,String newPassword);
} 