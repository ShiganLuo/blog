package com.baofeng.blog.service;

import com.baofeng.blog.common.util.minio.MinioUtil;
import com.baofeng.blog.dto.front.BookDTO.BookDetailResponse;
import com.baofeng.blog.dto.front.BookDTO.ChapterItem;
import com.baofeng.blog.dto.front.FrontArticleDTO.FrontArticle;
import com.baofeng.blog.mapper.ArticleMapper;
import com.baofeng.blog.mapper.BookMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 书架导出：把一本书打包为 ZIP（封面目录 + 逐章 Markdown + 图片）。
 * 图片一律走 MinioUtil.downloadFile 从对象存储拉取（容器内 minio:9000 可达），
 * 不通过公网 URL 下载，避免容器内 127.0.0.1 不通的问题。
 */
@Service
@RequiredArgsConstructor
public class BookExportService {

    private static final Logger logger = LoggerFactory.getLogger(BookExportService.class);

    /** Markdown 图片：![alt](url) */
    private static final Pattern MD_IMAGE = Pattern.compile("!\\[[^\\]]*\\]\\(([^)\\s]+)([^)]*)\\)");
    /** HTML 图片：<img ... src="url" ...> */
    private static final Pattern HTML_IMAGE = Pattern.compile("(<img[^>]*?\\ssrc=[\'\"])([^\'\"]+)([\'\"])");

    private final BookMapper bookMapper;
    private final ArticleMapper articleMapper;
    private final MinioUtil minioUtil;

    @Value("${minio.bucket}")
    private String bucket;

    /** 导出结果：文件名 + 内容 */
    public record ExportFile(String filename, byte[] data) {}

    /**
     * 导出 ZIP。onlyPublished=true 时只导出上架的书和公开的章节（前台口径）。
     * 书不存在返回 null。
     */
    public ExportFile exportZip(Long bookId, boolean onlyPublished) throws Exception {
        BookDetailResponse book = bookMapper.selectBookById(bookId, onlyPublished);
        if (book == null) {
            return null;
        }
        List<ChapterItem> chapters = bookMapper.selectChapters(bookId, onlyPublished);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos, StandardCharsets.UTF_8)) {
            // url/相对路径 -> zip 内图片路径（一次导出内去重）
            Map<String, String> imageZipPaths = new HashMap<>();

            // 封面图片
            String coverZipPath = null;
            if (book.getCoverImage() != null && !book.getCoverImage().isBlank()) {
                coverZipPath = downloadImage(book.getCoverImage(), 0, zos, imageZipPaths);
            }

            // 00 封面/目录
            StringBuilder toc = new StringBuilder();
            toc.append("# ").append(book.getTitle()).append("\n\n");
            if (coverZipPath != null) {
                toc.append("![](").append(coverZipPath).append(")\n\n");
            }
            if (book.getSubtitle() != null && !book.getSubtitle().isBlank()) {
                toc.append("> ").append(book.getSubtitle()).append("\n\n");
            }
            if (book.getDescription() != null && !book.getDescription().isBlank()) {
                toc.append(book.getDescription()).append("\n\n");
            }
            toc.append("---\n\n## 目录\n\n");
            int idx = 0;
            for (ChapterItem ch : chapters) {
                toc.append(++idx).append(". ").append(ch.getChapterTitle()).append("\n");
            }
            toc.append("\n---\n导出时间：")
               .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
               .append("\n");
            putEntry(zos, "00-" + safeName(book.getTitle()) + "-目录.md", toc.toString().getBytes(StandardCharsets.UTF_8));

            // 逐章
            idx = 0;
            for (ChapterItem ch : chapters) {
                idx++;
                FrontArticle article = articleMapper.getFrontArticleById(ch.getArticleId());
                if (article == null || article.getArticleContent() == null) {
                    logger.warn("导出跳过章节：articleId={} 内容为空", ch.getArticleId());
                    continue;
                }
                String content = resolveImages(article.getArticleContent(), idx, zos, imageZipPaths);
                String header = "## " + ch.getChapterTitle() + "\n\n";
                String entryName = String.format("%02d-%s.md", idx, safeName(ch.getChapterTitle()));
                putEntry(zos, entryName, (header + content).getBytes(StandardCharsets.UTF_8));
            }
        }
        return new ExportFile(safeName(book.getTitle()) + ".zip", baos.toByteArray());
    }

    /**
     * 把正文中的图片下载进 zip 并改写为相对路径；下载失败的保留原链接。
     */
    private String resolveImages(String content, int chapterIdx, ZipOutputStream zos,
                                 Map<String, String> imageZipPaths) {
        Set<String> refs = new HashSet<>();
        collectRefs(MD_IMAGE, content, 1, refs);
        collectRefs(HTML_IMAGE, content, 2, refs);
        for (String ref : refs) {
            String zipPath = downloadImage(ref, chapterIdx, zos, imageZipPaths);
            if (zipPath != null) {
                content = content.replace(ref, zipPath);
            }
        }
        return content;
    }

    private void collectRefs(Pattern pattern, String content, int group, Set<String> out) {
        Matcher m = pattern.matcher(content);
        while (m.find()) {
            out.add(m.group(group));
        }
    }

    /**
     * 下载单张图片到 zip 的 images/ 目录，返回 zip 内路径；失败返回 null。
     * 已下载过的直接复用。
     */
    private String downloadImage(String ref, int chapterIdx, ZipOutputStream zos,
                                 Map<String, String> imageZipPaths) {
        if (ref == null || ref.isBlank()) {
            return null;
        }
        String existing = imageZipPaths.get(ref);
        if (existing != null) {
            return existing;
        }
        String objectName = toObjectName(ref);
        if (objectName == null) {
            return null;
        }
        String baseName = objectName.substring(objectName.lastIndexOf('/') + 1);
        String zipPath = String.format("images/%02d_%d_%s", chapterIdx, imageZipPaths.size(), safeName(baseName));
        try (InputStream in = minioUtil.downloadFile(objectName)) {
            putEntry(zos, zipPath, in.readAllBytes());
            imageZipPaths.put(ref, zipPath);
            return zipPath;
        } catch (Exception e) {
            logger.warn("导出图片下载失败 ref={} objectName={}: {}", ref, objectName, e.getMessage());
            return null;
        }
    }

    /**
     * 引用字符串 → MinIO objectName。
     * 支持：/my-bucket/xx.png、my-bucket/xx.png、http(s)://host/my-bucket/xx.png（可带查询串）
     */
    private String toObjectName(String ref) {
        try {
            String path = ref.trim();
            int q = path.indexOf('?');
            if (q >= 0) {
                path = path.substring(0, q);
            }
            if (path.startsWith("http://") || path.startsWith("https://")) {
                path = new URI(path).getPath();
            }
            if (path == null || path.isBlank()) {
                return null;
            }
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            if (path.startsWith(bucket + "/")) {
                path = path.substring(bucket.length() + 1);
            }
            return path.isBlank() ? null : path;
        } catch (Exception e) {
            return null;
        }
    }

    private void putEntry(ZipOutputStream zos, String name, byte[] data) throws Exception {
        zos.putNextEntry(new ZipEntry(name));
        zos.write(data);
        zos.closeEntry();
    }

    /** 文件名安全化：去掉文件系统非法字符，限长 */
    private String safeName(String name) {
        if (name == null || name.isBlank()) {
            return "未命名";
        }
        String s = name.replaceAll("[\\\\/:*?\"<>|\\r\\n\\t]", "_").trim();
        if (s.length() > 80) {
            s = s.substring(0, 80);
        }
        return s.isBlank() ? "未命名" : s;
    }
}
