package com.baofeng.blog.common.util.file;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class FileUrlHelper {

    private final FileProperties properties;

    public FileUrlHelper(FileProperties properties) {
        this.properties = properties;
    }

    /**
     * path -> 完整访问 URL
     */
    public String toPublicUrl(String path) {
        if (!StringUtils.hasText(path)) {
            return null;
        }

        // 已经是完整 URL，直接返回（防止重复拼）
        if (isHttpUrl(path)) {
            return path;
        }

        String base = trimTrailingSlash(properties.getPublicBaseUrl());
        String cleanPath = trimLeadingSlash(path);

        return base + "/" + cleanPath;
    }

    /**
     * 提交值规范化：
     * - path        -> 原样返回
     * - 完整 URL    -> 剥离前缀，返回 path
     */
    // 两段式上传和一段式上传问题没解决
    public String normalizePath(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }

        if (!isHttpUrl(value)) {
            return trimLeadingSlash(value);
        }

        String base = trimTrailingSlash(properties.getPublicBaseUrl());

        if (!value.startsWith(base)) {
            throw new IllegalArgumentException("非法资源地址：" + value);
        }

        String path = value.substring(base.length());
        return trimLeadingSlash(path);
    }

    private boolean isHttpUrl(String value) {
        return value.startsWith("http://") || value.startsWith("https://");
    }

    private String trimLeadingSlash(String value) {
        return value.startsWith("/") ? value.substring(1) : value;
    }

    private String trimTrailingSlash(String value) {
        return value != null && value.endsWith("/")
                ? value.substring(0, value.length() - 1)
                : value;
    }
}

