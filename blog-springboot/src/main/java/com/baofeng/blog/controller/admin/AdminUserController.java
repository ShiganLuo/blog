package com.baofeng.blog.controller.admin;

import com.baofeng.blog.entity.User;
import com.baofeng.blog.service.UserService;
import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.admin.AdminLoginResponseVO;
import com.baofeng.blog.vo.admin.AdminUserAuthVO.*;
import com.baofeng.blog.vo.common.User.LoginRequest;
import com.baofeng.blog.vo.common.User.UserInfoResponse;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.fasterxml.jackson.databind.JsonNode;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {
    
    private final UserService userService;
    
    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<String> registerUser(@RequestBody @Valid RegisterRequest registerDTO) {
        //在Serivce写清楚了用户名重复如何处理的逻辑
        return userService.registerUser(registerDTO);
    }

    @PostMapping("/login")
    public ApiResponse<AdminLoginResponseVO> login(@RequestBody @Valid LoginRequest loginDTO) {
        return userService.loginUserAdmin(loginDTO);
    }

    //刷新token;是否需要判断只能用refreshToken来刷新token
    @PostMapping("/refreshToken")
    public ApiResponse<String> accessToeknGenerate(@RequestBody  Map<String, String> body){
        String rawToken = body.get("refreshToken");
        return userService.refreshToken(rawToken);
    }

    @GetMapping("/getUserInfoById/{id}")
    public ApiResponse<UserInfoResponse> getUserInfoById(@PathVariable Long id){
        return userService.getUserInfoById(id);
    }



    /**
     * 测试
     * 此接口要求请求携带有效的 JWT Token，JwtAuthenticationFilter 已经在请求处理前解析并设置好了认证信息
     * 当请求成功认证后，就可以通过 SecurityContextHolder 获取当前用户的用户名，再通过业务层查询数据库返回数据。
     */
    @GetMapping("/me")
    public ApiResponse<User> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ApiResponse.error(401, "未认证");
        }
        // 获取认证后的用户名
        String username = auth.getName();
        // 根据用户名查询数据库返回用户信息
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return ApiResponse.error(404, "未找到用户信息");
        }
        return ApiResponse.success(user);
    }

    @PostMapping("/updateUserRoles")
    public ApiResponse<String> updateUserRole(@RequestBody @Valid UpdateUserRoleRequest updaUserRoleRequest) {
        return userService.updateUserRole(updaUserRoleRequest);
    }

    @PostMapping("/getUsersList")
    public ApiResponse<userPageResponse> getUserList(@RequestBody userPageRequest request) {
        return userService.getUserList(request);
    }

    @GetMapping("/getUserInfoByToken")
    public ApiResponse<UserInfoResponse> getUserInfoByToken(@RequestHeader("Authorization") String BearerToken) {
        return userService.getUserInfoByToken(BearerToken);
    }

    @PostMapping("/passwordUpdate")
    public ApiResponse<String> updatePassword(@RequestBody JsonNode requestBody){
        String username = requestBody.get("username").asText(); // 从请求体中提取用户名
        String newPassword = requestBody.get("newPassword").asText(); // 提取新密码
        return userService.updatePassword(username, newPassword);
    }

    @PostMapping("/updateUserInfo")
    public ApiResponse<String> updateUserInfo(@RequestBody UpdateUserInfo updateUserInfo) {
        return userService.updateUserInfo(updateUserInfo);
    }

} 