package com.baofeng.blog.common.util;
import java.util.function.Supplier;

import org.springframework.data.redis.RedisConnectionFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SafeRedisExecutor {

    private static final Logger log = LoggerFactory.getLogger(SafeRedisExecutor.class);

    public static <T> T execute(Supplier<T> action, String bizMsg) {
        try {
            return action.get();
        } catch (RedisConnectionFailureException e) {
            log.warn("{}：Redis连接失败", bizMsg);
            log.debug("异常详情:", e);
        } catch (Exception e) {
            log.error("{}：Redis异常 - {}", bizMsg, e.getMessage());
            log.debug("异常详情:", e);
        }
        return null;
    }

    public static boolean execute(Runnable action, String bizMsg) {
        try {
            action.run();
            return true;
        } catch (RedisConnectionFailureException e) {
            log.warn("{}：Redis连接失败", bizMsg);
            log.debug("异常详情:", e);
        } catch (Exception e) {
            log.error("{}：Redis异常 - {}", bizMsg, e.getMessage());
            log.debug("异常详情:", e);
        }
        return false;
    }
}