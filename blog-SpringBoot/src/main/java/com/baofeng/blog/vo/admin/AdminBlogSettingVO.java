package com.baofeng.blog.vo.admin;
public class AdminBlogSettingVO {
    public record initSettingRequest(
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
    
}
