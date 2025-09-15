package com.baofeng.blog.entity;

import lombok.Data;
import java.time.LocalDateTime;


@Data
public class BlogSetting {
    private Long id;
    private String siteTitle;
    private String siteDescription;
    private String siteLogo;
    private String blogNotice;
    private String personalSay;
    private String giteeLink;
    private String bilibiliLink;
    private String githubLink;
    private String qqGroup;
    private String qqLink;
    private String wechatGroup;
    private String wechatLink;
    private String aliPay;
    private String wechatPay;
    private boolean enableComments;
    private long visitCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long user_id;
    private String site_url;

} 