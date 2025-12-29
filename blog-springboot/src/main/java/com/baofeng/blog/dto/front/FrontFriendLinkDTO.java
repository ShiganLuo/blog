package com.baofeng.blog.dto.front;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

public class FrontFriendLinkDTO {

    @Data
    public static class FrontAddFriendLinkRequest{
        private Long id;
        private String siteName;
        private String siteDesc;
        private String siteUrl;
        private String siteAvatar;
        private String siteLogo;
        private String applyMessage;
        @NotNull 
        private Long userId;
     }

    public static record FrontFriendLinkRequest(
        @NotNull Integer current,
        @NotNull Integer size
    ) {    }

    @Data
    public static class FrontFriendLinkPageResponse {
        private List<FrontFriendLinkItem> list;
        private Long total;
    }
    
    @Data
    public static class FrontFriendLinkItem {
        private Long id;
        private String siteAvatar;
        private String siteName;
        private String siteDesc;
        private String siteUrl;
        private Long userId;
    }
}
