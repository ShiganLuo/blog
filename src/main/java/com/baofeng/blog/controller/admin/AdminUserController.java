package com.baofeng.blog.controller.admin;

import com.baofeng.blog.entity.User;
import com.baofeng.blog.service.UserService;
import com.baofeng.blog.util.JwtTokenProvider;
import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.admin.AdminLoginResponseVO;
import com.baofeng.blog.vo.admin.AdminUserAuthVO.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {
    
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    
    @Autowired
    public AdminUserController(UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<User> registerUser(@RequestBody @Valid RegisterRequest registerDTO) {
        //在Serivce写清楚了用户名重复如何处理的逻辑
        return ApiResponse.success(userService.registerUser(registerDTO));
    }

    @PostMapping("/login")
    public ApiResponse<AdminLoginResponseVO> login(@RequestBody @Valid LoginRequest loginDTO) {
        User user = userService.loginUser(loginDTO);
        if (user != null) {
            // 生成 token
            //  accessTokenExpiration: 3600000    # 1 小时（60 分钟 = 60 * 60 * 1000 ms）
            //   refreshTokenExpiration: 1209600000  # 14 天（14 * 24 * 60 * 60 * 1000 ms
            String accessToken = jwtTokenProvider.generateToken(user,3600000,false);
            String refreshToken = jwtTokenProvider.generateToken(user, 1209600000,true);
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expires = now.plus(1, ChronoUnit.HOURS);
            // 构造 Token 和 User 信息（内部类）
            AdminLoginResponseVO.User userInfo = new AdminLoginResponseVO.User(
                    user.getAvatarUrl(),
                    user.getUsername(),
                    user.getNickName(),
                    user.getRole().name()
            );
            // 封装并返回
            AdminLoginResponseVO response = new AdminLoginResponseVO(accessToken,refreshToken,expires,userInfo);
            return ApiResponse.success(response);
        } else {
            return ApiResponse.error(401, "登录失败");
        }
    }
    //刷新token
    @PostMapping("/refreshToken")
    public ApiResponse<refreshTokenResponse> accessToeknGenerate(@RequestBody String rawToken){
        try {
            String refreshToken = rawToken.replaceAll("^\"|\"$", "");
            boolean success = jwtTokenProvider.isTokenExpired(refreshToken);
            if ( success ){
                String username = jwtTokenProvider.getUserNameFromToken(refreshToken);
                User user = userService.getUserByUsername(username);
                String accessToken = jwtTokenProvider.generateToken(user, 3600000,false);
                refreshTokenResponse response = new refreshTokenResponse();
                response.setAccessToken(accessToken);
                response.setRefreshToken(refreshToken);
                response.setExpires(LocalDateTime.now().plus(1, ChronoUnit.HOURS));
                return ApiResponse.success(response);
            } else {
                return ApiResponse.error(400, "refreshToken验证失败");
            }
        } catch( Exception e){
            return ApiResponse.error(400, "refreshToken验证失败"+ e.getMessage());
        }

    }
    @PostMapping("/getUserInfoById")
    public ApiResponse<User> getUserInfoById(@RequestBody JsonNode idRequest){
        Long id = idRequest.get("id").asLong(); 
        User user = userService.getUserInfoById(id);
        if (user != null){
            return ApiResponse.success(user);
        }else {
            return ApiResponse.error(401, "用户不存在");
        }
    }



    /**
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

    @PutMapping("/updateRole/{id}/{role}")
    public ApiResponse<String> updateUserRole(
            @PathVariable Long id, 
            @PathVariable String role) {
            boolean success = userService.updateUserRole(id, role);
            return success ? 
                ApiResponse.success("角色更新成功") : 
                ApiResponse.error(400, "角色更新失败");
    }
    @PostMapping("/getUsersList")
    public ApiResponse<userPageResponse> getUserList(@RequestBody userPageRequest request) {
        System.out.println("你好");
        return ApiResponse.success(userService.getUserList(request));
    }
    @GetMapping("/getUserInfoByToken")
    public ApiResponse<User> getUserInfoByToken(@RequestHeader("Authorization") String BearerToken) {
        String token = BearerToken.substring(7); // 去除 "Bearer " 前缀获取真正的 token
        // 验证 token 并获取用户名
        String username = jwtTokenProvider.getUserNameFromToken(token);

        if (username == null) {
            return ApiResponse.error(401, "无效的 token");
        }
        // 根据用户名查询数据库返回用户信息
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return ApiResponse.error(404, "未找到用户信息");
        }
        return ApiResponse.success(user);
    }
    @PostMapping("/passwordUpdate")
    public ApiResponse<String> updatePassword(@RequestBody JsonNode requestBody){
        String username = requestBody.get("username").asText(); // 从请求体中提取用户名
        String newPassword = requestBody.get("newPassword").asText(); // 提取新密码
    
        // 这里可以添加更新密码的逻辑
        boolean success = userService.updatePassword(username, newPassword);
        return success ? 
            ApiResponse.success("密码更新成功") : 
            ApiResponse.error(401, "密码更新失败");

    }

} 