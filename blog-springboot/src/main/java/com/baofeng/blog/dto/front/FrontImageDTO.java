package com.baofeng.blog.dto.front;

import com.baofeng.blog.common.annotation.MinioFile;

import lombok.Data;

public class FrontImageDTO {
    @Data
    public static class AlbumResponse {
        private Long id;
        private String album_name;
        @MinioFile
        private String album_cover;
    }
    
}
