package com.baofeng.blog.controller.front;

import com.baofeng.blog.service.RssService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/front")
public class RssController {

    private final RssService rssService;

    public RssController(RssService rssService) {
        this.rssService = rssService;
    }

    /**
     * 生成 RSS 订阅源
     * @return RSS XML
     */
    @GetMapping(value = "/rss", produces = MediaType.APPLICATION_XML_VALUE)
    public String getRss(HttpServletRequest request) {
        String baseUrl = getBaseUrl(request);
        return rssService.generateRss(baseUrl);
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
