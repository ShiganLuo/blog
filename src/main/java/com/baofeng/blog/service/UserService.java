package com.baofeng.blog.service;

import com.baofeng.blog.entity.User;
import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.admin.AdminLoginResponseVO;
import com.baofeng.blog.vo.admin.AdminUserAuthVO.*;
import com.baofeng.blog.vo.common.User.LoginRequest;
import com.baofeng.blog.vo.front.FrontUserVO.FrontLoginResponseVO;



public interface UserService {
    User registerUser(RegisterRequest registerDTO);
    /**
     * 登录
     * @param loginDTO
     * @return
     */
    public ApiResponse<FrontLoginResponseVO> loginUserFront(LoginRequest loginDTO);

    public ApiResponse<AdminLoginResponseVO> loginUserAdmin(LoginRequest loginDTO);

    User getUserByUsername(String username);
    /**
     * 根据id获取用户信息
     * @param id
     * @return
     */
    public ApiResponse<User> getUserInfoById(Long id);
    
    boolean updateUserRole(Long id, String role);
    userPageResponse getUserList(userPageRequest param);
    boolean updatePassword(String username,String newPassword);
} 