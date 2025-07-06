package com.baofeng.blog.vo.admin;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

public class AdminCategoryPageVO {
    /**
     * 创建响应目录字典
     */
    @Data
    public static class CategoryDictionaryResponse{
        long id;
        String name;
    }
    /**
     * 创建分类请求参数
     */
    public record CreateCategoryRequest(
        String name,         // 分类名称
        String description   // 分类描述
    ) {}

    /**
     * 分类分页请求参数
     */
    public record CategoryPageRequestVO(
        Integer pageNum,     // 当前页码
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
        private String name;         // 分类名称
        private String description;  // 分类描述
        private Integer articleCount; // 文章数量
        private LocalDateTime createTime;
        private LocalDateTime updateTime;
    }
} 