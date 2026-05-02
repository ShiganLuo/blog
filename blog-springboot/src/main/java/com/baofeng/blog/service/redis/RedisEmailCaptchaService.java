package com.baofeng.blog.service.redis;

import com.baofeng.blog.enums.RedisKeysEnum;
import com.baofeng.blog.common.util.SafeRedisExecutor;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisEmailCaptchaService {

    private final StringRedisTemplate redisTemplate;

    public RedisEmailCaptchaService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean saveEmailCaptcha(String email, String code) {
        String key = RedisKeysEnum.CAPTCHA_EMAIL_PREFIX.getKey() + email;
        return SafeRedisExecutor.execute(() -> {
            redisTemplate.opsForValue().set(key, code, RedisKeysEnum.CAPTCHA_EMAIL_PREFIX.getTtl());
        }, "保存邮箱验证码到Redis");
    }

    public boolean validateEmailCaptcha(String email, String input) {
        String key = RedisKeysEnum.CAPTCHA_EMAIL_PREFIX.getKey() + email;
        String realCode = SafeRedisExecutor.execute(() -> {
            return redisTemplate.opsForValue().get(key);
        }, "获取邮箱验证码");
        if (realCode != null && realCode.equals(input)) {
            SafeRedisExecutor.execute(() -> redisTemplate.delete(key), "删除邮箱验证码");
            return true;
        }
        return false;
    }
}
