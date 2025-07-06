package com.baofeng.blog.vo.admin;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

public class AdminCommentPageVO {
    
    /**
     * 评论分页请求参数
     */
    public record CommentPageRequestVO(
        Integer pageNum,     // 当前页码
        Integer pageSize,    // 每页显示条数
        String keyword,      // 可选，搜索关键词
        Long articleId,      // 可选，文章ID
        Long userId         // 可选，用户ID
    ) {}

    /**
     * 评论分页响应数据
     */
    @Data
    public static class CommentPageResponseVO {
        private long total;          // 总记录数
        private int pages;           // 总页数
        private List<CommentVO> list; // 评论列表
    }

    /**
     * 评论信息
     */
    @Data
    public static class CommentVO {
        private Long id;
        private String content;           // 评论内容
        private Long articleId;           // 文章ID
        private String articleTitle;      // 文章标题
        private Long userId;              // 用户ID
        private String userNickname;      // 用户昵称
        private String userAvatar;        // 用户头像
        private Long parentId;            // 父评论ID
        private String parentContent;     // 父评论内容（如果有）
        private LocalDateTime createTime;
        private LocalDateTime updateTime;
    }
} 