package com.baofeng.blog.entity;

import java.time.LocalDateTime;

import lombok.Data;
@Data
public class EntityImage {
    private Long id;
    private String entityType;
    private Long entityId;
    private Long imageId;
    private String usageType;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    
}
