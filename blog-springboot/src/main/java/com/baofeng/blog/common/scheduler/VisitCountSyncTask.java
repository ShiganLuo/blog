package com.baofeng.blog.common.scheduler;

import com.baofeng.blog.enums.RedisKeysEnum;
import com.baofeng.blog.entity.BlogSetting;
import com.baofeng.blog.mapper.ArticleMapper;
import com.baofeng.blog.mapper.BlogSettingMapper;
import com.baofeng.blog.common.util.SafeRedisExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.List;

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
     * 站点访问量同步（遍历所有用户）
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void syncSiteVisitCount() {
        List<BlogSetting> allSettings = blogSettingMapper.getAllSettings();
        if (allSettings == null || allSettings.isEmpty()) return;

        for (BlogSetting setting : allSettings) {
            Long userId = setting.getUserId();
            if (userId == null) continue;

            String key = SafeRedisExecutor.execute(() -> {
                return RedisKeysEnum.SITE_VISIT.getKey() + userId;
            }, "获取站点访问量 key");
            String value = SafeRedisExecutor.execute(() -> {
                return redisTemplate.opsForValue().get(key);
            }, "获取站点访问量");

            if (value == null) continue;

            int count = Integer.parseInt(value);
            if (count <= 0) continue;

            int rowsUpdated = blogSettingMapper.incrementVisitCountById(count, setting.getId());
            if (rowsUpdated == 0) {
                log.warn("Site visit sync failed, 0 row was updated, userId={}", userId);
            } else {
                log.info("Site visit synced, userId={}, settingId={}, +{}", userId, setting.getId(), count);
            }

            SafeRedisExecutor.execute(() -> {
                redisTemplate.delete(key);
            }, "删除站点访问量 Redis key");
        }
    }

    /**
     * 批量同步文章访问量
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void syncArticleVisitCount() {
        
        Set<String> keys = SafeRedisExecutor.execute(() -> {
            return redisTemplate.keys(RedisKeysEnum.ARTICLE_VISIT_PREFIX.getKey() + "*");
        }, "获取文章访问量 keys");

        if (keys == null || keys.isEmpty()) return;

        Map<Long, Long> articleVisitMap = new HashMap<>();

        // 遍历 Redis key 统计访问量
        for (String key : keys) {
            String value = SafeRedisExecutor.execute(() -> {
                return redisTemplate.opsForValue().get(key);
            }, "获取文章访问量");
            if (value == null) continue;

            long count = Long.parseLong(value);
            if (count <= 0) continue;

            Long articleId = Long.valueOf(key.substring(RedisKeysEnum.ARTICLE_VISIT_PREFIX.getKey().length()));
            articleVisitMap.put(articleId, count);
        }

        // 批量更新数据库
        if (!articleVisitMap.isEmpty()) {
            int updatedRows = articleMapper.batchIncrementArticleViews(articleVisitMap);
            log.info("Article visits synced, total articles: {}, rows updated: {}", articleVisitMap.size(), updatedRows);

            // 删除 Redis key
            for (Long articleId : articleVisitMap.keySet()) {
                SafeRedisExecutor.execute(() -> {
                    redisTemplate.delete(RedisKeysEnum.ARTICLE_VISIT_PREFIX.getKey() + articleId);
                }, "删除文章访问量 Redis key");
            }
        }
    }
}
