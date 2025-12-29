package com.baofeng.blog.dto.common;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

public class FriendLinkDTO {
    @Data
    public static class AddFriendLinkRequest{
        private Long id;
        private String siteName;
        private String siteDesc;
        private String siteUrl;
        private String siteLogo;
        private String applyMessage;
        @NotNull 
        private Long userId;
    }
}
