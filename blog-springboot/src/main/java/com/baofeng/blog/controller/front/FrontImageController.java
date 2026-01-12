package com.baofeng.blog.controller.front;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.front.FrontImageDTO.AlbumResponse;
import com.baofeng.blog.service.ImageService;
import com.baofeng.blog.dto.common.ImageDTO.ImageResponse;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/front/images")
public class FrontImageController {
    private final ImageService imageService;

    public FrontImageController (
        ImageService imageService
    ) {
        this.imageService = imageService;
    }

    /**
     * 获取所有相册信息
     * @return ApiResponse 包含相册信息的列表
     */
    @GetMapping("/getAllAlbum")
    public ApiResponse<List<AlbumResponse>> getAllAlbum() {
        return imageService.getAllAlbum();
    }

    @PostMapping("/uploadImage")
    public ApiResponse<ImageResponse> uploadImage(MultipartFile file) {
        return imageService.uploadImage(file);
    }
    
}
