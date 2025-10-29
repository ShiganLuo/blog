package com.baofeng.blog.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Role {
    private Long id;
    private String roleName;
    private String roleDesc;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}