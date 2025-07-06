package com.baofeng.blog.vo.front;

public class FrontBlogSettinVO {
    public record updateSettingRequest(
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
