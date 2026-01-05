package com.baofeng.blog.dto.common;

import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.*;
import lombok.Data;
// import lombok.Data;
public class ImageDTO {
    public static record UploadImage(
        @NotNull(message = "图片不能为空")
        MultipartFile file,
        @NotBlank(message = "实体类型不能为空")
        String entityType,
        @NotNull(message = "实体id不能为空")
        Long entityId,
        @NotBlank(message = "图片用途不能为空")
        String usageType
    ){}

    
    @Data
    public static class ImageResponse {
        private Long imageId;
        private String imageUrl;
    }
}
