package com.baofeng.blog.service.redis;

import com.baofeng.blog.enums.RedisKeysEnum;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisVisitCounter {

    private final StringRedisTemplate redisTemplate;

    public RedisVisitCounter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 站点访问量
    public void incrSiteVisit() {
        redisTemplate.opsForValue().increment(RedisKeysEnum.SITE_VISIT.getKey());
    }

    // 文章访问量
    public void incrArticleVisit(Long articleId) {
        redisTemplate.opsForValue()
                .increment(RedisKeysEnum.ARTICLE_VISIT_PREFIX.getKey() + articleId);
    }
}
