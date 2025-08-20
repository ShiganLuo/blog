package com.baofeng.blog.service.impl;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.List;

import com.baofeng.blog.service.ImageService;
import com.baofeng.blog.mapper.ImageMapper;
import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.front.FrontImageVO.AlbumResponse;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageMapper imageMapper;
    @Override
    public ApiResponse<List<AlbumResponse>> getAllAlbum() {
        // 调用Mapper方法获取所有相册信息
        List<AlbumResponse> albumList = imageMapper.selectAllAlbumsAbstract(null);
        
        if (albumList.isEmpty()) {
            return ApiResponse.error(404, "没有找到相册信息");
        }
        
        return ApiResponse.success(albumList);
    }

    
}
