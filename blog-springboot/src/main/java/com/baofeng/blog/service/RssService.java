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
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class RssService {

    private static final Logger logger = LoggerFactory.getLogger(RssService.class);
    private static final int MAX_ITEMS = 20;
    private static final DateTimeFormatter RSS_DATE_FORMAT = 
        DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z").withZone(ZoneId.of("GMT"));

    private final ArticleMapper articleMapper;
    private final BlogSettingMapper blogSettingMapper;

    public RssService(ArticleMapper articleMapper, BlogSettingMapper blogSettingMapper) {
        this.articleMapper = articleMapper;
        this.blogSettingMapper = blogSettingMapper;
    }

    /**
     * 生成 RSS 2.0 XML
     * @param baseUrl 网站基础URL
     * @return RSS XML 字符串
     */
    public String generateRss(String baseUrl) {
        try {
            // 获取博客设置
            BlogSetting setting = blogSettingMapper.getSettingById(1L);
            String title = setting != null ? setting.getWebsiteTitle() : "Blog";
            String description = setting != null ? setting.getWebsiteIntro() : "";
            String link = baseUrl;

            // 获取最新文章
            List<Map<String, Object>> articles = articleMapper.getRecentArticlesForRss(MAX_ITEMS);

            // 创建 RSS 文档
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            // RSS 根元素
            Element rss = doc.createElement("rss");
            rss.setAttribute("version", "2.0");
            rss.setAttribute("xmlns:atom", "http://www.w3.org/2005/Atom");
            doc.appendChild(rss);

            // Channel 元素
            Element channel = doc.createElement("channel");
            rss.appendChild(channel);

            // Channel 基本信息
            appendElement(doc, channel, "title", title);
            appendElement(doc, channel, "link", link);
            appendElement(doc, channel, "description", description);
            appendElement(doc, channel, "language", "zh-CN");
            appendElement(doc, channel, "lastBuildDate", RSS_DATE_FORMAT.format(java.time.Instant.now()));

            // Atom 自引用链接
            Element atomLink = doc.createElement("atom:link");
            atomLink.setAttribute("href", baseUrl + "/rss");
            atomLink.setAttribute("rel", "self");
            atomLink.setAttribute("type", "application/rss+xml");
            channel.appendChild(atomLink);

            // 添加文章条目
            for (Map<String, Object> article : articles) {
                Element item = doc.createElement("item");
                channel.appendChild(item);

                String articleTitle = getStringValue(article, "title");
                String articleSummary = getStringValue(article, "summary");
                Object articleId = article.get("id");
                Object createdAt = article.get("created_at");

                appendElement(doc, item, "title", articleTitle);
                appendElement(doc, item, "link", baseUrl + "/article/" + articleId);
                appendElement(doc, item, "guid", baseUrl + "/article/" + articleId);
                appendElement(doc, item, "description", articleSummary != null ? articleSummary : "");

                if (createdAt != null) {
                    String dateStr;
                    if (createdAt instanceof java.time.LocalDateTime ldt) {
                        dateStr = RSS_DATE_FORMAT.format(ldt.atZone(ZoneId.systemDefault()).toInstant());
                    } else if (createdAt instanceof java.util.Date date) {
                        dateStr = RSS_DATE_FORMAT.format(date.toInstant());
                    } else {
                        dateStr = RSS_DATE_FORMAT.format(java.time.Instant.now());
                    }
                    appendElement(doc, item, "pubDate", dateStr);
                }
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
            logger.error("生成 RSS 失败", e);
            return generateEmptyRss();
        }
    }

    private void appendElement(Document doc, Element parent, String tagName, String textContent) {
        Element element = doc.createElement(tagName);
        element.setTextContent(textContent != null ? textContent : "");
        parent.appendChild(element);
    }

    private String getStringValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }

    private String generateEmptyRss() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
               "<rss version=\"2.0\">\n" +
               "<channel>\n" +
               "<title>Blog</title>\n" +
               "<description></description>\n" +
               "</channel>\n" +
               "</rss>";
    }
}
