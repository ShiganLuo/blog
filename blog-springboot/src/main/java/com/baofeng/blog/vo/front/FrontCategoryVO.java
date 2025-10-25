package com.baofeng.blog.vo.front;

import lombok.Data;
public class FrontCategoryVO {
    @Data
    public static class CategoryDictionaryResponse {
        private long id;
        private String categoryName;
    }
}
