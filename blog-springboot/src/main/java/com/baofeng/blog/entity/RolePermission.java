package com.baofeng.blog.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class RolePermission {
    private Long id;
    private Long roleId;
    private Long permissionId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
