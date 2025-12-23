package com.baofeng.blog.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.TimeUnit;

@Configuration
public class CaffeineCacheConfig {

    @Bean
    public Cache<String, String> emailCodeCache() {
        return Caffeine.newBuilder()
            // 关键：设置最后一次写入后 5 分钟过期
            .expireAfterWrite(5, TimeUnit.MINUTES)
            // 关键：针对 1G 内存服务器，限制最大条数（例如 1000 条）
            // 达到上限后，Caffeine 会根据算法自动剔除不常用的数据
            .maximumSize(1000)
            .build();
    }
}