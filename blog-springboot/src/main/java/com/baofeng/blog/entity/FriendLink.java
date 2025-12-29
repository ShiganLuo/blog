package com.baofeng.blog.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class FriendLink {
    private Long id;
    private String siteName;
    private String siteDesc;
    private String siteLogo;
    private String siteUrl;
    private Long userId;
    private Integer status;
    private Integer sortOrder;
    private Boolean isVisible;
    private String applyMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
