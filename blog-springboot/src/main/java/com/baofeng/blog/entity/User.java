package com.baofeng.blog.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    
    // 用户名（唯一约束）
    private String username;
    
    // 邮箱（唯一约束）
    private String email;
    
    private String password;
    
    // 数据库字段 avatar_url
    private String avatarUrl;
    
    private String bio;
    
    // 昵称,数据库字段nick_name
    private String nickName;
    
    // 枚举类型需要实现自定义类型处理器 或 使用@EnumValue注解（MyBatis Plus）
    private Role role;
    
    private Status status;
    
    // 数据库字段 created_at
    private LocalDateTime createdAt;
    
    // 数据库字段 updated_at
    private LocalDateTime updatedAt;
    
    private LocalDateTime lastLogin;
    
    private Integer loginAttempts;
    
    // 数据库字段 is_email_verified
    private Boolean emailVerified;
    
    // 数据库字段 is_active
    private Boolean active;

    public enum Role {
        USER, ADMIN
    }

    public enum Status {
        ACTIVE, INACTIVE, BANNED
    }
}