package com.baofeng.blog.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.front.FrontImageDTO.AlbumResponse;
import com.baofeng.blog.dto.common.ImageDTO.ImageResponse;
import com.baofeng.blog.entity.Image;

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

    /**
     * 分页查询图片列表（支持按文件名搜索）
     * @param fileName 文件名关键词（可选）
     * @return 图片列表
     */
    public ApiResponse<List<Image>> listImages(String fileName);
    
}
