package com.baofeng.blog.service.redis;

import com.baofeng.blog.enums.RedisKeysEnum;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisEmailCaptchaService {

    private final StringRedisTemplate redisTemplate;

    public RedisEmailCaptchaService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveEmailCaptcha(String email, String code) {
        String key = RedisKeysEnum.CAPTCHA_EMAIL_PREFIX.getKey() + email;
        redisTemplate.opsForValue().set(key, code, RedisKeysEnum.CAPTCHA_EMAIL_PREFIX.getTtl());
    }

    public boolean validateEmailCaptcha(String email, String input) {
        String key = RedisKeysEnum.CAPTCHA_EMAIL_PREFIX.getKey() + email;
        String realCode = redisTemplate.opsForValue().get(key);
        if (realCode != null && realCode.equals(input)) {
            redisTemplate.delete(key);
            return true;
        }
        return false;
    }
}
