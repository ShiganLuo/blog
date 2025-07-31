package com.baofeng.blog.vo.admin;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

public class AdminTagPageVO {
    /**
     * 创建响应标签字典
     */
    @Data
    public static class TagDictionaryResponse{
        Long id;
        String name;
    }
    /**
     * 创建标签请求参数
     */
    public record CreateTagRequest(
        String name         // 标签名称
    ) {}

    /**
     * 标签分页请求参数
     */
    public record TagPageRequestVO(
        Integer pageNum,     // 当前页码
        Integer pageSize,    // 每页显示条数
        String keyword      // 可选，搜索关键词
    ) {}

    /**
     * 标签分页响应数据
     */
    @Data
    public static class TagPageResponseVO {
        private long total;          // 总记录数
        private int pages;           // 总页数
        private List<TagVO> list;    // 标签列表
    }

    /**
     * 标签信息
     */
    @Data
    public static class TagVO {
        private Long id;
        private String name;         // 标签名称
        private Integer articleCount; // 文章数量
        private LocalDateTime createTime;
        private LocalDateTime updateTime;
    }
} 