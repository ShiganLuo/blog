package com.baofeng.blog.common.util.file;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "file")
public class FileProperties {

    /**
     * 对外访问基础地址（CDN / 域名）
     * 例如：https://cdn.example.com
     */
    private String publicBaseUrl;

    public String getPublicBaseUrl() {
        return publicBaseUrl;
    }

    public void setPublicBaseUrl(String publicBaseUrl) {
        this.publicBaseUrl = publicBaseUrl;
    }
}

