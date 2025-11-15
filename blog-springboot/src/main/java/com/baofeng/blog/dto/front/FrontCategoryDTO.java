package com.baofeng.blog.dto.front;

import lombok.Data;
public class FrontCategoryDTO {
    @Data
    public static class CategoryDictionaryResponse {
        private long id;
        private String categoryName;
    }
}
