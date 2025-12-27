package com.baofeng.blog.enums;
import java.time.Duration;

public enum RedisKeysEnum {

    // 站点访问量
    SITE_VISIT("SITE:VISIT:", Duration.ofDays(365)),

    // 文章访问量（后面加文章ID）
    ARTICLE_VISIT_PREFIX("ARTICLE:VISIT:", Duration.ofDays(365)),

    // 图片验证码（后面加UUID）
    CAPTCHA_IMAGE_PREFIX("CAPTCHA:IMG:", Duration.ofMinutes(5)),

    // 邮箱验证码（后面加邮箱）
    CAPTCHA_EMAIL_PREFIX("CAPTCHA:EMAIL:", Duration.ofMinutes(5));

    private final String keyPrefix;
    private final Duration ttl;

    RedisKeysEnum(String keyPrefix, Duration ttl) {
        this.keyPrefix = keyPrefix;
        this.ttl = ttl;
    }

    public String getKey() {
        return keyPrefix;
    }

    public Duration getTtl() {
        return ttl;
    }
}