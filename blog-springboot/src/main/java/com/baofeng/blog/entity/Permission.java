package com.baofeng.blog.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Permission {
    private Long id;
    private String name;
    private String permission;
    private String type;
    private Long parentId;
    private String path;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
