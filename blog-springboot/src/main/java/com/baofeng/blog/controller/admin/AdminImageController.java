package com.baofeng.blog.controller.admin;



import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.service.ImageService;
import com.baofeng.blog.dto.common.ImageDTO.ImageResponse;
import com.baofeng.blog.entity.Image;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/admin/image")
public class AdminImageController {
    private final ImageService imageService;

    public AdminImageController (
        ImageService imageService
    ) {
        this.imageService = imageService;
    }

    @PostMapping("/uploadImage")
    public ApiResponse<ImageResponse> uploadImage(@RequestPart("file") MultipartFile file) {
        return imageService.uploadImage(file);
    }

    /**
     * 获取图片列表（支持按文件名搜索）
     * @param fileName 文件名关键词（可选）
     * @return 图片列表
     */
    @GetMapping("/list")
    public ApiResponse<List<Image>> listImages(
            @RequestParam(required = false) String fileName) {
        return imageService.listImages(fileName);
    }
}
