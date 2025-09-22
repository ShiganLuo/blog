package com.baofeng.blog.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class CaptchaConfig {

    @Bean
    public DefaultKaptcha kaptchaProducer() {
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        properties.setProperty("kaptcha.border", "yes"); // 图片边框
        properties.setProperty("kaptcha.border.color", "105,179,90");
        properties.setProperty("kaptcha.textproducer.font.color", "blue"); // 字体颜色
        properties.setProperty("kaptcha.textproducer.char.space", "5"); // 字符间距
        properties.setProperty("kaptcha.image.width", "150"); // 图片宽
        properties.setProperty("kaptcha.image.height", "50"); // 图片高
        properties.setProperty("kaptcha.textproducer.font.size", "40"); // 字体大小
        properties.setProperty("kaptcha.textproducer.char.length", "5"); // 验证码长度
        Config config = new Config(properties);
        kaptcha.setConfig(config);
        return kaptcha;
    }
}
