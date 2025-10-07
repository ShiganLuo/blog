package com.baofeng.blog.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;
    private String email;   
    private String password;
    private String avatarUrl;    
    private String bio;
    private String nickName;
    private String phoneNumber;
    private Integer gender;   
    private String status;   
    private LocalDateTime createdAt;   
    private LocalDateTime updatedAt;   
    private LocalDateTime lastLogin;   
    private Integer loginAttempts;    
    private Boolean isEmailVerified;
    private Boolean isActive;
}