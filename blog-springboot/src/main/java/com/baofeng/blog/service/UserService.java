package com.baofeng.blog.service;

import org.springframework.web.multipart.MultipartFile;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.admin.AdminLoginResponseDTO;
import com.baofeng.blog.dto.admin.AdminUserAuthDTO.*;
import com.baofeng.blog.dto.common.ImageDTO.ImageResponse;
import com.baofeng.blog.dto.common.UserDTO.LoginRequest;
import com.baofeng.blog.dto.common.UserDTO.UserInfoResponse;
import com.baofeng.blog.dto.front.FrontUserDTO.FrontLoginResponseVO;
import com.baofeng.blog.entity.User;



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
    public ApiResponse<AdminLoginResponseDTO> loginUserAdmin(LoginRequest loginDTO);

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
    public ApiResponse<UserPageResponse> getUserList(UserPageRequest userPageRequest);

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
    public ApiResponse<String> updatePassword(UpdatePasswordRequest updatePasswordRequest);

    /**
     * 更新用户信息
     * @param updateUserInfo
     * @return
     */
    public ApiResponse<String> updateUserInfo(UpdateUserInfo updateUserInfo);

    /**
     * 删除用户
     * @param userId
     * @return
     */
    public ApiResponse<String> deleteUser(Long userId);

    /**
     * 更新用户头像
     * @param id
     * @param avatarUrl
     * @return
     */
    public ApiResponse<ImageResponse> updateUserAvatar(Long userId, MultipartFile avatarFile);

} 