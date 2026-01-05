package com.baofeng.blog.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.front.FrontImageDTO.AlbumResponse;
import com.baofeng.blog.dto.common.ImageDTO.ImageResponse;

public interface ImageService {

    /**
     * 获取所有图片
     * @return
     */
    public ApiResponse<List<AlbumResponse>> getAllAlbum();

    /**
     * 上传图片，返回url
     * @return
     */
    public ApiResponse<ImageResponse> uploadImage(MultipartFile file);
    
}
