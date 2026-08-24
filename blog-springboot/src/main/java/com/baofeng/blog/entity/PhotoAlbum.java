package com.baofeng.blog.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PhotoAlbum {
    private Long id;
    private String albumName;
    private String description;
    private String albumCover;
    private Integer sortOrder;
    private Boolean isVisible;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
