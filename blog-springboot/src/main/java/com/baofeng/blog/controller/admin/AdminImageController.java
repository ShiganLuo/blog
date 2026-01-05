package com.baofeng.blog.controller.admin;



import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.service.ImageService;
import com.baofeng.blog.dto.common.ImageDTO.ImageResponse;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/image")
@Validated
public class AdminImageController {
    private final ImageService imageService;

    public AdminImageController (
        ImageService imageService
    ) {
        this.imageService = imageService;
    }

    @PostMapping("/uploadImage")
    public ApiResponse<ImageResponse> uploadImage(@RequestPart MultipartFile file) {
        return imageService.uploadImage(file);
    }
}
