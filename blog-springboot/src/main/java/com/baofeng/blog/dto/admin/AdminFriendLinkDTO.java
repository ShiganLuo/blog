package com.baofeng.blog.dto.admin;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotNull;

public class AdminFriendLinkDTO {

    public static record AdminFriendLinkRequest(
        @NotNull 
        Integer current,
        @NotNull 
        Integer size,
        String keyword
    ) {    }
    @Data
    public static class AdminFriendLinkPageResponse {
        private List<AdminFriendLinkItem> list;
        private Long total;
    }

    @Data 
    public static class AdminFriendLinkItem {
        private Long id;
        private Long userId;
        private String siteName;
        private String siteLogo;
        private String siteUrl;
        private String siteDesc;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
    
}
