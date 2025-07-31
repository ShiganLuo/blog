package com.baofeng.blog.vo.front;

import lombok.Data;

@Data
public class FrontTagVO {
    /**
     * 创建响应标签字典
     */
    @Data
    public static class TagDictionaryResponse{
        private Long id;
        private String tag_name;
    }

} 