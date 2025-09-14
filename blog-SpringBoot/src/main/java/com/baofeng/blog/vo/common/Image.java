package com.baofeng.blog.vo.common;

import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.*;
// import lombok.Data;
public class Image {
    public record UploadImage(
        @NotNull(message = "图片不能为空")
        MultipartFile file,
        @NotBlank(message = "实体类型不能为空")
        String entityType,
        @NotNull(message = "实体id不能为空")
        Long entityId,
        @NotBlank(message = "图片用途不能为空")
        String usageType
    ){}
}
