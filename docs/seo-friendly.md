# 搜索引擎优化（SEO）操作文档

本文档记录拾感日记博客的 SEO 架构设计、已实施的优化措施，以及后续操作指南。

## 一、已实施的优化

### 1. robots.txt

**作用**：告诉搜索引擎爬虫哪些页面可以抓取，同时指向 sitemap 位置。

**实现**：后端 `SeoController` 提供 `/robots.txt` 端点，返回纯文本。

**内容**：
```
User-agent: *
Allow: /
Sitemap: https://shiganluo.top/sitemap.xml
```

**nginx 配置**：在 `try_files` 之前用 `location = /robots.txt` 精确匹配，直接代理到后端，避免被 SPA fallback 拦截返回 HTML。

### 2. sitemap.xml

**作用**：列出网站所有重要页面 URL，帮助搜索引擎高效发现和索引内容。

**实现**：后端 `SitemapService` 动态生成，查询数据库中所有已发布文章。

**包含页面**：
- 首页（`/`）— priority 1.0，daily 更新
- 文章列表页（`/articleList`）— priority 0.8，weekly 更新
- 归档页（`/archives`）— priority 0.5，monthly 更新
- 所有已发布文章（`/article?id=xxx`）— priority 0.8，weekly 更新

**nginx 配置**：同 robots.txt，`location = /sitemap.xml` 精确匹配代理到后端。

### 3. 安全白名单

`/robots.txt` 和 `/sitemap.xml` 已加入 Spring Security 白名单（`application-*.yml`），无需登录即可访问。

### 4. 页面级动态 meta 标签

**作用**：每个文章页有独立的标题、描述、关键词，搜索引擎结果页（SERP）会展示这些信息。社交分享（微信/Twitter/Telegram）也需要 OG 标签来生成预览卡片。

**实现**：前端使用 `@vueuse/head` 库，在文章组件中动态设置 `<head>` 标签。

**包含标签**：
| 标签 | 说明 |
|------|------|
| `<title>` | 文章标题 - 拾感日记 |
| `<meta name="description">` | 文章摘要（优先 articleDescription，否则截取正文前 160 字） |
| `<meta name="keywords">` | 文章标签列表 |
| `<meta property="og:title">` | Open Graph 标题 |
| `<meta property="og:description">` | Open Graph 描述 |
| `<meta property="og:image">` | Open Graph 封面图 |
| `<meta property="og:type">` | `article` |
| `<meta property="og:url">` | 当前页面 URL |
| `<meta name="twitter:card">` | Twitter Card 类型 |
| `<link rel="canonical">` | 规范链接，防止重复内容 |

### 5. JSON-LD 结构化数据

**作用**：让 Google 搜索结果展示富摘要（Rich Snippet），如文章标题、作者、发布时间等。

**实现**：在文章页 `<head>` 中注入 `application/ld+json` 脚本，使用 Schema.org `Article` 类型。

**数据结构**：
```json
{
  "@context": "https://schema.org",
  "@type": "Article",
  "headline": "文章标题",
  "description": "文章摘要",
  "image": "封面图URL",
  "author": { "@type": "Person", "name": "作者名" },
  "datePublished": "发布时间",
  "dateModified": "更新时间",
  "mainEntityOfPage": { "@type": "WebPage", "@id": "文章URL" }
}
```

### 6. HTML lang 属性

`<html lang="zh-CN">` — 告诉搜索引擎页面主要语言是中文，有助于中文搜索结果排名。

### 7. RSS 订阅

已有 `/api/front/rss/{userId}` 端点，`<link rel="alternate" type="application/rss+xml">` 在 index.html 中声明。RSS 读者和聚合器可以自动发现新文章。

## 二、部署后必做操作

### 1. Google Search Console

1. 访问 https://search.google.com/search-console
2. 添加资源 `shiganluo.top`（推荐用「网址前缀」方式）
3. 验证方式：HTML 文件验证（上传验证文件到 nginx 静态目录）或 DNS 验证
4. 提交站点地图：`https://shiganluo.top/sitemap.xml`
5. 使用「网址检查」工具，输入关键文章 URL，点击「请求编入索引」

### 2. 百度站长平台（可选）

1. 访问 https://ziyuan.baidu.com
2. 添加站点，验证所有权
3. 提交 sitemap：`https://shiganluo.top/sitemap.xml`
4. 使用「链接提交」工具主动推送新文章

### 3. 验证 SEO 端点

部署后用以下命令验证：
```bash
# 检查 robots.txt
curl https://shiganluo.top/robots.txt

# 检查 sitemap.xml
curl https://shiganluo.top/sitemap.xml

# 检查文章页 meta 标签（在浏览器中查看源代码）
# 搜索 <meta name="description" 和 <meta property="og:
```

### 4. Google Rich Results Test

访问 https://search.google.com/test/rich-results ，输入文章 URL，验证 JSON-LD 结构化数据是否被正确识别。

## 三、后续可做的优化

| 优先级 | 优化项 | 说明 |
|--------|--------|------|
| 高 | 服务端渲染（SSR） | 当前是 SPA，Google 能爬但不如 SSR 友好。迁移到 Nuxt 3 或 vite-ssg 可显著改善 |
| 中 | 文章 URL 美化 | 当前 `/article?id=123` 改为 `/article/my-title-slug` 对 SEO 更友好 |
| 中 | 页面加载速度 | 图片懒加载、CDN 加速、关键 CSS 内联 |
| 低 | 多语言 hreflang | 如果未来有英文版，需要加 hreflang 标签 |
| 低 | 外链建设 | 在其他网站获取反向链接是提升排名的核心因素 |

## 四、文件变更清单

### 后端新增
- `blog-springboot/.../controller/front/SeoController.java`
- `blog-springboot/.../service/SitemapService.java`

### 后端修改
- `blog-springboot/.../mapper/ArticleMapper.java` — 新增 `getAllPublishedArticlesForSitemap()`
- `blog-springboot/.../mapper/ArticleMapper.xml` — 新增 SQL 查询
- `blog-springboot/src/main/resources/application-dev.yml` — 白名单加 robots.txt/sitemap.xml
- `blog-springboot/src/main/resources/application-prod.yml` — 同上
- `blog-springboot/src/main/resources/application-template.yml` — 同上

### 前端修改
- `blog-vue3/blog-vue3-front/package.json` — 新增 `@vueuse/head` 依赖
- `blog-vue3/blog-vue3-front/index.html` — `lang="zh-CN"`
- `blog-vue3/blog-vue3-front/src/main.ts` — 注册 `createHead` 插件
- `blog-vue3/blog-vue3-front/src/views/article/article.vue` — 动态 meta/OG/JSON-LD

### Nginx
- `blog-vue3/blog-vue3-front/nginx.conf` — 新增 `/robots.txt` 和 `/sitemap.xml` location 块
