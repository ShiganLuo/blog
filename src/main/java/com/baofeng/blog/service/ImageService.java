package com.baofeng.blog.service;

import java.util.List;

import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.front.FrontImageVO.AlbumResponse;

public interface ImageService {

    public ApiResponse<List<AlbumResponse>> getAllAlbum();
    
}
