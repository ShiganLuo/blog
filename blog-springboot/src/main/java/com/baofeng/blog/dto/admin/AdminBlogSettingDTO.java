package com.baofeng.blog.dto.admin;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

public class AdminBlogSettingDTO {
    public static record InitSettingRequest(
        String websiteChineseName,
        String websiteEnglishName,
        String websiteTitle,
        LocalDateTime websiteCreaDateTime,
        String logo,
        String favicon,
        String notice,
        String icpFilingNumber,
        String psbFilingNumber,
        String author,
        String authorAvatar,
        String authorIntro,
        String userAvatar,
        String github,
        String gitee,
        String bilibili,
        String qq,
        String qqGroup,
        String wechat,
        String wechatGroup,
        String weibo,
        String csdn,
        String zhihu,
        String juejin,
        String twitter,
        String stackoverflow,
        String touristAvatar,
        Integer multiLanguage,
        Boolean isCommentReview,
        Boolean isEmailNotice,
        String weiXinQRCode,
        String alipayQRCode
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

    @Data
    public static class AdminConfigDetail {
        private String websiteChineseName;
        private String websiteEnglishName;
        private String websiteTitle;
        private String websiteIntro;
        private LocalDateTime websiteCreateTime;
        private String frontHeadBackground;
        private String logo;
        private String favicon;
        private String notice;
        private String icpFilingNumber;
        private String psbFilingNumber;
        private String author;
        private String authorAvatar;
        private String authorIntro;
        private String authroPersonalSay;
        private String userAvatar;
        private String github;
        private String gitee;
        private String bilibili;
        private String qq;
        private String qqGroup;
        private String wechat;
        private String wechatGroup;
        private String weibo;
        private String csdn;
        private String zhihu;
        private String juejin;
        private String twitter;
        private String stackoverflow;
        private String touristAvatar;
        private Integer multiLanguage;
        private Boolean isCommentReview;
        private Boolean isEmailNotice;
        private String weiXinQRCode;
        private String alipayQRCode;
    }
}
