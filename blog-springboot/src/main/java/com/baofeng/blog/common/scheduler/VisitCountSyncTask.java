package com.baofeng.blog.common.scheduler;

import com.baofeng.blog.enums.RedisKeysEnum;
import com.baofeng.blog.mapper.ArticleMapper;
import com.baofeng.blog.mapper.BlogSettingMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class VisitCountSyncTask {

    private final StringRedisTemplate redisTemplate;
    private final BlogSettingMapper blogSettingMapper;
    private final ArticleMapper articleMapper;

    public VisitCountSyncTask(
            StringRedisTemplate redisTemplate,
            BlogSettingMapper blogSettingMapper,
            ArticleMapper articleMapper
    ) {
        this.redisTemplate = redisTemplate;
        this.blogSettingMapper = blogSettingMapper;
        this.articleMapper = articleMapper;
    }

    /**
     * 站点访问量同步
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void syncSiteVisitCount() {
        Long blogSettingId = 1L; // 单站点固定 ID，可扩展多站点

        String key = RedisKeysEnum.SITE_VISIT.getKey() + blogSettingId;
        String value = redisTemplate.opsForValue().get(key);

        if (value == null) return;

        long count = Long.parseLong(value);
        if (count <= 0) return;

        int rowsUpdated = blogSettingMapper.incrementVisitCountById(blogSettingId, count);
        if (rowsUpdated == 0) {
            log.warn("Site visit sync failed, 0 row was updated, blogSettingId={}", blogSettingId);
        } else {
            log.info("Site visit synced, blogSettingId={}, +{}", blogSettingId, count);
        }

        redisTemplate.delete(key);
    }

    /**
     * 批量同步文章访问量
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void syncArticleVisitCount() {
        Set<String> keys = redisTemplate.keys(RedisKeysEnum.ARTICLE_VISIT_PREFIX.getKey() + "*");

        if (keys == null || keys.isEmpty()) return;

        Map<Long, Long> articleVisitMap = new HashMap<>();

        // 遍历 Redis key 统计访问量
        for (String key : keys) {
            String value = redisTemplate.opsForValue().get(key);
            if (value == null) continue;

            long count = Long.parseLong(value);
            if (count <= 0) continue;

            Long articleId = Long.valueOf(key.substring(RedisKeysEnum.ARTICLE_VISIT_PREFIX.getKey().length()));
            articleVisitMap.put(articleId, count);
        }

        // 批量更新数据库
        if (!articleVisitMap.isEmpty()) {
            int updatedRows = articleMapper.batchIncrementVisitCount(articleVisitMap);
            log.info("Article visits synced, total articles: {}, rows updated: {}", articleVisitMap.size(), updatedRows);

            // 删除 Redis key
            for (Long articleId : articleVisitMap.keySet()) {
                redisTemplate.delete(RedisKeysEnum.ARTICLE_VISIT_PREFIX.getKey() + articleId);
            }
        }
    }
}
