package com.baofeng.blog.service;

import com.baofeng.blog.entity.User;
import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.admin.AdminLoginResponseVO;
import com.baofeng.blog.vo.admin.AdminUserAuthVO.*;
import com.baofeng.blog.vo.common.User.LoginRequest;
import com.baofeng.blog.vo.front.FrontUserVO.FrontLoginResponseVO;
import com.baofeng.blog.vo.common.User.UserInfoResponse;



public interface UserService {
    /**
     * 用户注册
     * @param registerDTO
     * @return
     */
    public ApiResponse<String> registerUser(RegisterRequest registerDTO);
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
    public ApiResponse<String> refreshToken(String rawToken);

    User getUserByUsername(String username);
    /**
     * 根据id获取用户信息
     * @param id
     * @return
     */
    public ApiResponse<UserInfoResponse> getUserInfoById(Long id);
    
    /**
     * 更新用户角色
     * @param updateUserRoleRequest
     * @return
     */
    public ApiResponse<String> updateUserRole(UpdateUserRoleRequest updateUserRoleRequest);

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
    public ApiResponse<UserInfoResponse> getUserInfoByToken(String BearerToken);

    /**
     * 更新用户密码
     * @param username
     * @param newPassword
     * @return
     */
    public ApiResponse<String> updatePassword(String username,String newPassword);
} 