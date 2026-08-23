package com.baofeng.blog.common.util;

import java.util.regex.Pattern;

/**
 * URL 规范化工具：
 * 1. 去除 http / https / // 开头的域名或 IP 前缀
 * 2. 数据库存储阶段统一转为相对路径
 *
 * 示例：
 * https://example.com/upload/a.png   -> /upload/a.png
 * http://127.0.0.1:8080/img/a.jpg    -> /img/a.jpg
 * //cdn.example.com/static/a.svg     -> /static/a.svg
 * /upload/a.png                     -> /upload/a.png
 */
public final class UrlNormalizeUtil {

    /**
     * 匹配：
     *  - http://xxx
     *  - https://xxx
     *  - //xxx
     *
     * xxx 支持：
     *  - 域名
     *  - IPv4
     *  - IPv4:port
     *  - 域名:port
     */
    private static final Pattern URL_PREFIX_PATTERN = Pattern.compile(
            "^(https?:)?//" +                  // 协议或 //
            "(" +
                "([a-zA-Z0-9.-]+)" +            // 域名
                "|" +
                "(\\d{1,3}(\\.\\d{1,3}){3})" +  // IPv4
            ")" +
            "(?::\\d{1,5})?"                   // 可选端口
    );

    private UrlNormalizeUtil() {
    }

    /**
     * 去除 URL 前缀，仅保留路径部分
     *
     * @param url 原始 URL 或路径
     * @return 相对路径
     */
    public static String stripUrlPrefix(String url) {
        if (url == null || url.isBlank()) {
            return url;
        }

        // 已经是相对路径，直接返回
        if (url.startsWith("/")) {
            return url;
        }

        return URL_PREFIX_PATTERN.matcher(url).replaceFirst("");
    }
}