package com.baofeng.blog.dto.admin;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AdminCategoryPageDTO {

    /**
     * 创建分类请求参数
     */
    public record CreateCategoryRequest(
        @NotNull(message = "分类名称不能为空")
        String name,         // 分类名称
        String description   // 分类描述
    ) {}

    /**
     * 分类分页请求参数
     */
    public record CategoryPageRequestVO(
        @NotNull(message = "页码不能为空")
        @Min(value = 1, message = "页码必须大于等于1")
        Integer pageNum,     // 当前页码
        
        @NotNull(message = "每页显示条数不能为空")
        @Min(value = 1, message = "每页显示条数必须大于等于1")
        Integer pageSize,    // 每页显示条数
        String keyword      // 可选，搜索关键词
    ) {}

    /**
     * 分类分页响应数据
     */
    @Data
    public static class CategoryPageResponseVO {
        private long total;          // 总记录数
        private int pages;           // 总页数
        private List<CategoryVO> list; // 分类列表
    }

    /**
     * 分类信息
     */
    @Data
    public static class CategoryVO {
        private Long id;
        private String categoryName;         // 分类名称
        private Integer articleCount; // 文章数量
        private LocalDateTime createTime;
        private LocalDateTime updateTime;
    }

    @Data
    public static class CategoryDictionaryResponse {
        private long id;
        private String categoryName;
    }
} 