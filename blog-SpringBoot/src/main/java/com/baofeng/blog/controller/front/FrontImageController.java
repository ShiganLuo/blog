package com.baofeng.blog.controller.front;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import java.util.List;

import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.front.FrontImageVO.AlbumResponse;
import com.baofeng.blog.service.ImageService;

@RestController
@RequestMapping("/api/front/images")
@RequiredArgsConstructor
@Validated
public class FrontImageController {
    private final ImageService imageService;

    /**
     * 获取所有相册信息
     * @return ApiResponse 包含相册信息的列表
     */
    @GetMapping("/getAllAlbum")
    public ApiResponse<List<AlbumResponse>> getAllAlbum() {
        return imageService.getAllAlbum();
    }

    @PostMapping("/uploadImage")
    public ApiResponse<String> uploadImage(MultipartFile file) {
        return imageService.uploadImage(file);
    }
    
}
