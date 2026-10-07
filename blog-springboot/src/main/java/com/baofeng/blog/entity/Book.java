package com.baofeng.blog.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Book {
    private Long id;
    private String title;
    private String subtitle;
    private String description;
    private String coverImage;
    private Long authorId;
    private Integer status;
    private Boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
