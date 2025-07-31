package com.baofeng.blog.vo.front;
import lombok.Data;
import java.time.LocalDateTime;

public class FrontBlogSettinVO {
    public record updateSettingRequest(
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
    public static class configDetail {
        private String blog_name;
        private String avatar_bg;
        private String personal_say;
        private String blog_notice;
        private String blog_avatar;
        private String qq_group;
        private String we_chat_group;
        private String ali_pay;
        private String we_chat_pay;
        private Long view_time;
        private Long articleCount;
        private Long categoryCount;
        private Long tagCount;
        private String git_ee_link;
        private String bilibili_link;
        private String github_link;
        private String we_chat_link;
        private String qq_link;
        private LocalDateTime createdAt;
        
    }
}
