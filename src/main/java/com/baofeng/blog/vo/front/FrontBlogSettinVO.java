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
        String blog_name;
        String avatar_bg;
        String personal_say;
        String blog_notice;
        String blog_avatar;
        String qq_group;
        String we_chat_group;
        String ali_pay;
        String we_chat_pay;
        Long view_time;
        Long articleCount;
        Long categoryCount;
        Long tagCount;
        String git_ee_link;
        String bilibili_link;
        String github_link;
        String we_chat_link;
        String qq_link;
        LocalDateTime createdAt;
        
    }
}
