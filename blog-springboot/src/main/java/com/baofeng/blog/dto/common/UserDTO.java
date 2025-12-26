package com.baofeng.blog.dto.common;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

public class UserDTO {
    // 登录请求
    public static record LoginRequest(
        @NotBlank(message = "账号不能为空")
        String username,
        
        @NotBlank(message = "密码不能为空")
        String password
    ) {}

    @Data
    @Builder
    public static class UserInfoResponse {
        private Long userId;
        private String userName;
        private String email;
        private String password;
        private String avatarUrl;
        private String bio;
        private String nickName;
        private String phoneNumber;
        private Integer sex;
        private List<String> roles;
        private String status; 
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime lastLogin;
        private Integer loginAttempts;
        private Boolean isEmailVerified;
    }
}
