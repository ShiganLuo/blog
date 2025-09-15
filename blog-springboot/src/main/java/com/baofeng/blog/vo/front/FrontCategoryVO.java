package com.baofeng.blog.vo.front;

import lombok.Data;
public class FrontCategoryVO {
    @Data
    public static class CategoryResponse {
        private Long id;
        private String category_name;       
    }
}
