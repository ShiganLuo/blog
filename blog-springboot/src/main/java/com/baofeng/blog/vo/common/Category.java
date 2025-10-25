package com.baofeng.blog.vo.common;

import lombok.Data;

public class Category {
    /**
     * 创建响应目录字典
     */
    @Data
    public static class CategoryDictionaryResponse{
        private long id;
        private String categoryName;
    }
}
