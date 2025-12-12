package com.baofeng.blog.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.service.ImageService;

import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/api/admin/image")
@RequiredArgsConstructor
@Validated
public class AdminImageController {
    private final ImageService imageService;
    @PostMapping("/uploadImage")
    public ApiResponse<String> uploadImage(@RequestPart MultipartFile file) {
        return imageService.uploadImage(file);
    }
}
