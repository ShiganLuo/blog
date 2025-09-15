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
     * 前台登录
     * @param loginDTO
     * @return
     */
    public ApiResponse<FrontLoginResponseVO> loginUserFront(LoginRequest loginDTO);

    /**
     * 后台登录
     * @param loginDTO
     * @return
     */
    public ApiResponse<AdminLoginResponseVO> loginUserAdmin(LoginRequest loginDTO);

    /**
     * 刷新token
     * @param rawToken
     * @return
     */
    public ApiResponse<refreshTokenResponse> refreshToken(String rawToken);

    User getUserByUsername(String username);
    /**
     * 根据id获取用户信息
     * @param id
     * @return
     */
    public ApiResponse<User> getUserInfoById(Long id);
    
    public ApiResponse<String> updateUserRole(Long id, String role);

    /**
     * 获取用户列表
     * @param param
     * @return
     */
    public ApiResponse<userPageResponse> getUserList(userPageRequest param);

    /**
     * 根据token获取用户信息
     * @param BearerToken
     * @return
     */
    public ApiResponse<User> getUserInfoByToken(String BearerToken);

    /**
     * 更新用户密码
     * @param username
     * @param newPassword
     * @return
     */
    public ApiResponse<String> updatePassword(String username,String newPassword);
} 