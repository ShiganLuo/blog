package com.baofeng.blog.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Like {
    private Long id;
    private Long userId;
    private String targetType;
    private Long targetId;
    private Boolean status;
    private LocalDateTime createdAt;
}
