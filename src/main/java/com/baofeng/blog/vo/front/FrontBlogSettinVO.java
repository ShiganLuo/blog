package com.baofeng.blog.vo.front;
import lombok.Data;
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

    @Data
    public static class configDetail {
        String blog_notice;
        String qq_group;
        String we_chat_group;
        String ali_pay;
        String we_chat_pay;
        Long view_time;
        Long articleCount;    
        
    }
}
