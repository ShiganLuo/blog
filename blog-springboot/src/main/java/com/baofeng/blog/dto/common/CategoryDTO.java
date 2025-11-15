package com.baofeng.blog.dto.common;

import lombok.Data;

public class CategoryDTO {
    /**
     * 创建响应目录字典
     */
    @Data
    public static class CategoryDictionaryResponse{
        private long id;
        private String categoryName;
    }
}
