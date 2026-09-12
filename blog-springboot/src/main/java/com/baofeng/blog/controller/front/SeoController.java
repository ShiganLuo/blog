package com.baofeng.blog.controller.front;

import com.baofeng.blog.service.SitemapService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SeoController {

    private final SitemapService sitemapService;

    public SeoController(SitemapService sitemapService) {
        this.sitemapService = sitemapService;
    }

    /**
     * robots.txt - 搜索引擎爬虫指引
     */
    @GetMapping(value = "/robots.txt", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getRobotsTxt(HttpServletRequest request) {
        return sitemapService.generateRobotsTxt(getBaseUrl(request));
    }

    /**
     * sitemap.xml - 站点地图，帮助搜索引擎发现页面
     */
    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public String getSitemap(HttpServletRequest request) {
        return sitemapService.generateSitemap(getBaseUrl(request));
    }

    private String getBaseUrl(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();

        StringBuilder url = new StringBuilder();
        url.append(scheme).append("://").append(serverName);

        if (("http".equals(scheme) && serverPort != 80) ||
            ("https".equals(scheme) && serverPort != 443)) {
            url.append(":").append(serverPort);
        }

        return url.toString();
    }
}
