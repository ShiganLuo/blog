package com.baofeng.blog.entity;

import lombok.Data;
import java.time.LocalDateTime;

import com.baofeng.blog.common.advice.CustomLocalDateTimeDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


@Data
public class BlogSetting {
    private Long id;
    private String websiteChineseName;
    private String websiteEnglishName;
    private String websiteTitle;
    private String websiteIntro;
    private String frontHeadBackground;
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime websiteCreateTime;
    private String logo;
    private String favicon;
    private String notice;
    private String icpFilingNumber;
    private String psbFilingNumber;
    private String author;
    private String authorAvatar; // 网站作者头像
    private String authorIntro;
    private String authorPersonalSay;
    private String userAvatar; // 用户默认头像
    private String touristAvatar; // 游客默认头像
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
    private String multiLanguage;
    private Boolean isCommentReview;
    private Boolean isEmailNotice;
    private String wechatQrCode;
    private String alipayQrCode;
    private Long visitCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 