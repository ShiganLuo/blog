package com.baofeng.blog.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Comment {
    private Long id;
    private Long fromId; // 评论人用户ID
    private String content; // 评论文本内容
    private Long forId; // 被评论对象ID（文章ID或评论ID）
    private Long parentId; // 父评论ID（二级评论用）
    private Long likes; // 点赞数
    private Boolean isDelated; // 逻辑删除标志
    private Boolean status; // 评论审核状态
    private LocalDateTime createdAt; // 评论创建时间
    private LocalDateTime updatedAt; // 评论修改时间
} 