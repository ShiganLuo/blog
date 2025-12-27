package com.baofeng.blog.controller.admin;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.service.ImageService;

import org.springframework.validation.annotation.Validated;

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
    public ApiResponse<String> uploadImage(@RequestPart MultipartFile file) {
        return imageService.uploadImage(file);
    }
}
