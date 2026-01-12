package com.baofeng.blog.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Comment {
    private Long id;
    private Long userId; // 评论人用户ID
    private String content; // 评论文本内容
    private Long forId; // 被评论对象ID（文章ID或评论ID）
    private String type; // 评论对象类型（post=文章评论，comment=回复评论）
    private Long replyUserId; // 被回复用户ID
    private Long authorId; // 文章作者ID，当type为post时有效
    private Long likes; // 点赞数
    private Boolean isDeleted; // 逻辑删除标志
    private Boolean status; // 评论审核状态
    private LocalDateTime createdAt; // 评论创建时间
    private LocalDateTime updatedAt; // 评论修改时间
    private String ipAddress; // IP地址
    private Long rootId; // 根评论ID（方便查询）
    private String tag; // message类型独有
} 