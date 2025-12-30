package com.baofeng.blog.dto.front;

import java.util.List;

import com.baofeng.blog.common.annotation.MinioFile;
import com.baofeng.blog.common.annotation.MinioScan;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

public class FrontFriendLinkDTO {


    public static record FrontFriendLinkRequest(
        @NotNull Integer current,
        @NotNull Integer size
    ) {    }

    @Data
    @MinioScan(maxDepth = 2)
    public static class FrontFriendLinkPageResponse {
        private List<FrontFriendLinkItem> list;
        private Long total;
    }
    
    @Data
    public static class FrontFriendLinkItem {
        private Long id;
        @MinioFile
        private String siteLogo;
        private String siteName;
        private String siteDesc;
        private String siteUrl;
        private Long userId;
    }
}
