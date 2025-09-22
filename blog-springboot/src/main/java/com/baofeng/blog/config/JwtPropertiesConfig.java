package com.baofeng.blog.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.List;
import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "security.jwt")
public class JwtPropertiesConfig {
    private List<String> whitelist;
    private String tokenHeader;
    private String tokenHead;
    private String secret;
    private Long accessTokenExpiration;
    private Long refreshTokenExpiration;
}