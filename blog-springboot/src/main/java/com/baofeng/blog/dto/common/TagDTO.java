package com.baofeng.blog.dto.common;

import lombok.Data;

public class TagDTO {
    @Data
    public static class TagDictionaryResponse{
        private Long id;
        private String tagName;
    }

}
