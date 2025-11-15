package com.baofeng.blog.dto.front;

import lombok.Data;

public class FrontImageDTO {
    @Data
    public static class AlbumResponse {
        private Long id;
        private String album_name;
        private String album_cover;
    }
    
}
