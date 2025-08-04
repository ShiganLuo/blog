package com.baofeng.blog.vo.common;

import lombok.Data;

public class Tag {
    @Data
    public static class TagDictionaryResponse{
        private Long id;
        private String tag_name;
    }

}
