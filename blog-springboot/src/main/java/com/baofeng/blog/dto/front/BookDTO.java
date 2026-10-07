package com.baofeng.blog.dto.front;

import com.baofeng.blog.common.annotation.MinioFile;
import com.baofeng.blog.dto.front.FrontArticleDTO.ArticleDetailResponse;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 书架（书 + 章节）前后台共用 DTO，结构参照 PhotoAlbumDTO
 */
public class BookDTO {

    /** 书列表项 */
    @Data
    public static class BookListResponse {
        private Long id;
        private String title;
        private String subtitle;
        private String description;
        @MinioFile
        private String coverImage;
        private Integer status;
        private Integer chapterCount;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    /** 书详情（含章节目录） */
    @Data
    public static class BookDetailResponse {
        private Long id;
        private String title;
        private String subtitle;
        private String description;
        @MinioFile
        private String coverImage;
        private Integer status;
        private List<ChapterItem> chapters;
    }

    /** 章节目录项 */
    @Data
    public static class ChapterItem {
        private Long articleId;
        private String chapterTitle;
        private String articleTitle;
        private Integer sortOrder;
    }

    /** 章正文 + 上下章指针 */
    @Data
    public static class ChapterResponse {
        private Long bookId;
        private String bookTitle;
        private int chapterIndex;      // 从1开始
        private int totalChapters;
        private ArticleDetailResponse content;
        private ChapterRef prev;
        private ChapterRef next;
    }

    /** 上/下章引用 */
    @Data
    public static class ChapterRef {
        private Long articleId;
        private String chapterTitle;
    }

    /** 新建书 */
    @Data
    public static class AddBookRequest {
        private String title;
        private String subtitle;
        private String description;
        private String coverImage;
    }

    /** 修改书（含上架状态） */
    @Data
    public static class UpdateBookRequest {
        private Long id;
        private String title;
        private String subtitle;
        private String description;
        private String coverImage;
        private Integer status;
    }

    /** 批量加入章节 */
    @Data
    public static class AddArticlesRequest {
        private Long bookId;
        private List<Long> articleIds;
    }

    /** 移除章节 */
    @Data
    public static class RemoveArticleRequest {
        private Long bookId;
        private Long articleId;
    }

    /** 章节整序（传入完整有序 articleId 列表） */
    @Data
    public static class ReorderRequest {
        private Long bookId;
        private List<Long> orderedArticleIds;
    }

    /** 重命名章节 */
    @Data
    public static class RenameChapterRequest {
        private Long bookId;
        private Long articleId;
        private String chapterTitle;
    }
}
