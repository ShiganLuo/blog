package com.baofeng.blog.service;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.front.PhotoAlbumDTO.*;
import java.util.List;

public interface PhotoAlbumService {

    ApiResponse<List<AlbumListResponse>> getAllAlbums();

    ApiResponse<AlbumDetailResponse> getAlbumById(Long id);

    ApiResponse<String> addAlbum(AddAlbumRequest request);

    ApiResponse<String> updateAlbum(UpdateAlbumRequest request);

    ApiResponse<String> deleteAlbum(Long id);

    ApiResponse<String> addImageToAlbum(AddImageToAlbumRequest request);

    ApiResponse<String> removeImageFromAlbum(RemoveImageFromAlbumRequest request);
}
