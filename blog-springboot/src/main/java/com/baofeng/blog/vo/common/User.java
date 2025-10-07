package com.baofeng.blog.vo.common;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

public class User {
    // 登录请求
    public record LoginRequest(
        @NotBlank(message = "账号不能为空")
        String username,
        
        @NotBlank(message = "密码不能为空")
        String password
    ) {}

    @Data
    @Builder
    public static class UserInfoResponse {
        private Long id;
        private String username;
        private String email;
        private String password;
        private String avatarUrl;
        private String bio;
        private String nickName;
        private String phoneNumber;
        private String gender;
        private List<String> roles;
        private String status; 
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime lastLogin;
        private Integer loginAttempts;
        private Boolean isEmailVerified;
        private Boolean isActive;
    }
}
