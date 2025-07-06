package com.baofeng.blog.vo.admin;
public class AdminBlogSettingVO {
    public record initSettingRequest(
        String siteTitle,
        String siteDescription,
        String siteLogo,
        String qqGroup,
        String wechatGroup,
        String aliPay,
        String wechatPay,
        boolean enableComments
    ) {}
    
}
