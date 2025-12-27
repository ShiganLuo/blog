package com.baofeng.blog.service.redis;

import com.baofeng.blog.enums.RedisKeysEnum;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisCaptchaService {

    private final StringRedisTemplate redisTemplate;

    public RedisCaptchaService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveImageCaptcha(String uuid, String text) {
        String key = RedisKeysEnum.CAPTCHA_IMAGE_PREFIX.getKey() + uuid;
        redisTemplate.opsForValue().set(key, text, RedisKeysEnum.CAPTCHA_IMAGE_PREFIX.getTtl());
    }

    public boolean validateCaptcha(String uuid, String input) {
        String key = RedisKeysEnum.CAPTCHA_IMAGE_PREFIX.getKey() + uuid;
        String realText = redisTemplate.opsForValue().get(key);
        if (realText != null && realText.equalsIgnoreCase(input)) {
            redisTemplate.delete(key);
            return true;
        }
        return false;
    }
}

