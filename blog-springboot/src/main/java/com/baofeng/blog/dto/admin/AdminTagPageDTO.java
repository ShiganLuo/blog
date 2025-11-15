package com.baofeng.blog.dto.admin;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AdminTagPageDTO {
    /**
     * 创建标签请求参数
     */
    public record CreateTagRequest(
        @NotNull(message = "标签名称不能为空")
        String name         // 标签名称
    ) {}

    /**
     * 标签分页请求参数
     */
    public record TagPageRequestVO(
        @NotNull(message = "页码不能为空")
        @Min(value = 1, message = "页码必须大于等于1")
        Integer pageNum,     // 当前页码
        
        @NotNull(message = "每页显示条数不能为空")
        @Min(value = 1, message = "每页显示条数必须大于等于1")
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
        private String tagName;         // 标签名称
        private Integer articleCount; // 文章数量
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
} 