package com.baofeng.blog.vo.front;

import lombok.Data;

public class FrontImageVO {
    @Data
    public static class AlbumResponse {
        private Long id;
        private String album_name;
        private String album_cover;
    }
    
}
