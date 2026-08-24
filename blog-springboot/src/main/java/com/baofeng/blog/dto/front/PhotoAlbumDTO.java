package com.baofeng.blog.dto.front;

import lombok.Data;
import java.util.List;

public class PhotoAlbumDTO {

    @Data
    public static class AlbumListResponse {
        private Long id;
        private String albumName;
        private String description;
        private String albumCover;
        private Integer photoCount;
    }

    @Data
    public static class AlbumDetailResponse {
        private Long id;
        private String albumName;
        private String description;
        private String albumCover;
        private List<AlbumPhoto> photos;
    }

    @Data
    public static class AlbumPhoto {
        private Long imageId;
        private String filePath;
        private String fileName;
        private Integer sortOrder;
    }

    @Data
    public static class AddAlbumRequest {
        private String albumName;
        private String description;
        private String albumCover;
    }

    @Data
    public static class UpdateAlbumRequest {
        private Long id;
        private String albumName;
        private String description;
        private String albumCover;
        private Integer sortOrder;
        private Boolean isVisible;
    }

    @Data
    public static class AddImageToAlbumRequest {
        private Long albumId;
        private Long imageId;
    }

    @Data
    public static class RemoveImageFromAlbumRequest {
        private Long albumId;
        private Long imageId;
    }
}
