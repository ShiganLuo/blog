package com.baofeng.blog.service.redis;

import com.baofeng.blog.enums.RedisKeysEnum;
import com.baofeng.blog.common.util.SafeRedisExecutor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisVisitCounter {

    Long blogSettingId = 1L;

    private final StringRedisTemplate redisTemplate;

    public RedisVisitCounter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 站点访问量
    public void incrSiteVisit() {
        SafeRedisExecutor.execute(() -> {
            redisTemplate.opsForValue().increment(RedisKeysEnum.SITE_VISIT.getKey() + blogSettingId);
        }, "增加站点访问量");
    }

    // 文章访问量
    public void incrArticleVisit(Long articleId) {
        SafeRedisExecutor.execute(() -> {
            redisTemplate.opsForValue().increment(RedisKeysEnum.ARTICLE_VISIT_PREFIX.getKey() + articleId);
        }, "增加文章访问量");
    }
}
