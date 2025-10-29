package com.baofeng.blog.vo.admin;

import java.util.List;
import lombok.Data;

public class AdminBlogSettingVO {
    public record InitSettingRequest(
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
    public static class SystemSettingDict {
        String dictLabel;
        String dictValue;
        String listClass; // e.g., "info", "success", "warning", "danger"
        String cssClass; // e.g., "custom-tag-class"
    }

    @Data
    public static class SystemSettingDictResponse {
        Integer total;
        List<SystemSettingDict> list;
    }
}
