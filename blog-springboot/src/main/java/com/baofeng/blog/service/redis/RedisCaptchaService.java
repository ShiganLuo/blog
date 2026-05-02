package com.baofeng.blog.service.redis;

import com.baofeng.blog.enums.RedisKeysEnum;
import com.baofeng.blog.common.util.SafeRedisExecutor;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Component
public class RedisCaptchaService {

    private final StringRedisTemplate redisTemplate;
    private static final Logger logger = LoggerFactory.getLogger(RedisCaptchaService.class);

    public RedisCaptchaService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveImageCaptcha(String uuid, String text) {
        String key = RedisKeysEnum.CAPTCHA_IMAGE_PREFIX.getKey() + uuid;
        SafeRedisExecutor.execute(() -> {
            redisTemplate.opsForValue().set(key, text, RedisKeysEnum.CAPTCHA_IMAGE_PREFIX.getTtl());
        }, "保存验证码到Redis");
    }

    public boolean validateCaptcha(String uuid, String input) {
        String key = RedisKeysEnum.CAPTCHA_IMAGE_PREFIX.getKey() + uuid;
        String realText = SafeRedisExecutor.execute(() -> {
            return redisTemplate.opsForValue().get(key);
        }, "获取验证码");
        if (realText != null && realText.equalsIgnoreCase(input)) {
            SafeRedisExecutor.execute(() -> {
                redisTemplate.delete(key);
            }, "验证验证码");
            return true;
        }
        logger.warn("验证码验证失败，uuid={}, input={}", uuid, input);
        return false;
    }
}

