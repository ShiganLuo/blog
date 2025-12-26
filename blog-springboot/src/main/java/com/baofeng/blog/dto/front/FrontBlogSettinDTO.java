package com.baofeng.blog.dto.front;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.validation.constraints.NotNull;
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
    public static class ConfigDetail {
        private String websiteTitle;
        private String FrontHeadBackground;
        private String personal_say;
        private String notice;
        private String logo;
        private String ICPFilingNumber;
        private String PSBFilingNumber;
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

    public static record FriendLinkRequest(
        @NotNull Integer current,
        @NotNull Integer size
    ) {    }

    @Data
    public static class FriendLinkPackResponse {
        private List<FriendLinkResponse> list;
        private Long total;
    }
    
    @Data
    public static class FriendLinkResponse {
        private Long id;
        private String site_avatar;
        private String site_name;
        private String site_desc;
        private String site_url;
        private Long user_id;
    }

    public static record AddFriendLinkRequest(
        Long id,
        String site_name,
        String site_desc,
        String site_url,
        String site_avatar,
        @NotNull Long user_id
    ) { }
}
