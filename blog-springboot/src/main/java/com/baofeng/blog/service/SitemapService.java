package com.baofeng.blog.service;

import com.baofeng.blog.entity.BlogSetting;
import com.baofeng.blog.mapper.ArticleMapper;
import com.baofeng.blog.mapper.BlogSettingMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class SitemapService {

    private static final Logger logger = LoggerFactory.getLogger(SitemapService.class);
    private static final DateTimeFormatter SITEMAP_DATE_FORMAT = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final ArticleMapper articleMapper;
    private final BlogSettingMapper blogSettingMapper;

    public SitemapService(ArticleMapper articleMapper, BlogSettingMapper blogSettingMapper) {
        this.articleMapper = articleMapper;
        this.blogSettingMapper = blogSettingMapper;
    }

    /**
     * 生成 sitemap.xml
     * @param baseUrl 网站基础URL
     * @return sitemap XML 字符串
     */
    public String generateSitemap(String baseUrl) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            // urlset 根元素
            Element urlset = doc.createElement("urlset");
            urlset.setAttribute("xmlns", "http://www.sitemaps.org/schemas/sitemap/0.9");
            doc.appendChild(urlset);

            // 首页
            addUrl(doc, urlset, baseUrl + "/", LocalDateTime.now(), "daily", "1.0");

            // 文章列表页
            addUrl(doc, urlset, baseUrl + "/articleList", LocalDateTime.now(), "weekly", "0.8");

            // 归档页
            addUrl(doc, urlset, baseUrl + "/archives", LocalDateTime.now(), "monthly", "0.5");

            // 所有已发布文章
            List<Map<String, Object>> articles = articleMapper.getAllPublishedArticlesForSitemap();
            for (Map<String, Object> article : articles) {
                Object articleId = article.get("id");
                Object updatedAt = article.get("updated_at");
                LocalDateTime lastMod = toLocalDateTime(updatedAt);
                addUrl(doc, urlset, baseUrl + "/article?id=" + articleId, lastMod, "weekly", "0.8");
            }

            // 转换为字符串
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");

            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            return writer.toString();

        } catch (Exception e) {
            logger.error("生成 sitemap 失败", e);
            return generateEmptySitemap();
        }
    }

    /**
     * 生成 robots.txt 内容
     * @param baseUrl 网站基础URL
     * @return robots.txt 内容
     */
    public String generateRobotsTxt(String baseUrl) {
        return "User-agent: *\n" +
               "Allow: /\n" +
               "\n" +
               "Sitemap: " + baseUrl + "/sitemap.xml\n";
    }

    private void addUrl(Document doc, Element urlset, String loc, LocalDateTime lastMod, 
                        String changefreq, String priority) {
        Element url = doc.createElement("url");
        urlset.appendChild(url);

        Element locEl = doc.createElement("loc");
        locEl.setTextContent(loc);
        url.appendChild(locEl);

        if (lastMod != null) {
            Element lastModEl = doc.createElement("lastmod");
            lastModEl.setTextContent(SITEMAP_DATE_FORMAT.format(lastMod));
            url.appendChild(lastModEl);
        }

        Element changefreqEl = doc.createElement("changefreq");
        changefreqEl.setTextContent(changefreq);
        url.appendChild(changefreqEl);

        Element priorityEl = doc.createElement("priority");
        priorityEl.setTextContent(priority);
        url.appendChild(priorityEl);
    }

    private LocalDateTime toLocalDateTime(Object value) {
        if (value instanceof LocalDateTime ldt) {
            return ldt;
        } else if (value instanceof java.util.Date date) {
            return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        }
        return null;
    }

    private String generateEmptySitemap() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
               "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n" +
               "</urlset>";
    }
}
