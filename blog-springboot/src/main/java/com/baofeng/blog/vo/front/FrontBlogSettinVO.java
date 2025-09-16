package com.baofeng.blog.vo.front;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

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
        private Long userCount;
        private String git_ee_link;
        private String bilibili_link;
        private String github_link;
        private String we_chat_link;
        private String qq_link;
        private LocalDateTime createdAt;
        
    }

    public record FriendLinkRequest(
        Integer current,
        Integer size
    ) {
        public FriendLinkRequest {
            if (current == null) {
                throw new IllegalArgumentException("current不能为空");
            }
            if (size == null) {
                throw new IllegalArgumentException("size不能为空");
            }
        }
    }
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

    public record AddFriendLinkRequest(
        Long id,
        String site_name,
        String site_desc,
        String site_url,
        String site_avatar,
        Long user_id
    ) {
        public AddFriendLinkRequest {
            if (user_id == null) {
                throw new IllegalArgumentException("user_id不能为空");
            }
        }
    }
}
