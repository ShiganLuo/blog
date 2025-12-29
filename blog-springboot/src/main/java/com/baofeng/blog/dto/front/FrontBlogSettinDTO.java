package com.baofeng.blog.dto.front;
import lombok.Data;
import java.time.LocalDateTime;
public class FrontBlogSettinDTO {
    public static record updateSettingRequest(
        String siteTitle,
        String siteDescription,
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
    public static class FrontConfigDetail {
        private String websiteTitle;
        private String FrontHeadBackground;
        private String personal_say;
        private String notice;
        private String authorAvatar;
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


}
