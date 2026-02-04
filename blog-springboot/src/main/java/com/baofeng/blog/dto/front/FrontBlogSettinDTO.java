package com.baofeng.blog.dto.front;
import lombok.Data;
import java.time.LocalDateTime;

import com.baofeng.blog.common.annotation.MinioFile;
public class FrontBlogSettinDTO {
    public static record UpdateSettingRequest(
        String siteTitle,
        String siteDescription,
        @MinioFile
        String siteLogo,
        String blogNotice,
        String personalSay,
        String giteeLink,
        String bilibiliLink,
        String githubLink,
        String qqGroup,
        String qqLink,
        String wechatGroup,
        String wechatLink,
        String aliPay,
        String wechatPay,
        boolean enableComments
    ) {}

    @Data
    public static class FrontConfigDetailResponse {
        private String websiteTitle;
        @MinioFile
        private String FrontHeadBackground;
        private String personal_say;
        private String notice;
        @MinioFile
        private String authorAvatar;
        @MinioFile
        private String logo;
        private String icpFilingNumber;
        private String psbFilingNumber;
        private String blog_intro;
        private String qq_group;
        private String we_chat_group;
        private String ali_pay;
        private String we_chat_pay;
        private Long view_time;
        private Long articleCount;
        private Long categoryCount;
        private Long tagCount;
        private Long userCount;
        private String gitee;
        private String bilibili;
        private String github;
        private String weChat;
        private String qq;
        private LocalDateTime createdAt;
        
    }

    @Data
    public static class SomeFrontInformation {
        private String icpFilingNumber;
        private String psbFilingNumber;
        private String websiteChineseName;
        @MinioFile
        private String favicon;
    }


}
