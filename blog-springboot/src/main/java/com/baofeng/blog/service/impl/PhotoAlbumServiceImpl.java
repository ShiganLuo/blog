package com.baofeng.blog.service.impl;

import com.baofeng.blog.common.util.UrlNormalizeUtil;
import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.front.PhotoAlbumDTO.*;
import com.baofeng.blog.entity.PhotoAlbum;
import com.baofeng.blog.mapper.PhotoAlbumMapper;
import com.baofeng.blog.service.PhotoAlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PhotoAlbumServiceImpl implements PhotoAlbumService {

    private final PhotoAlbumMapper photoAlbumMapper;

    @Override
    public ApiResponse<List<AlbumListResponse>> getAllAlbums() {
        List<AlbumListResponse> albums = photoAlbumMapper.selectAllAlbums();
        return ApiResponse.success(albums);
    }

    @Override
    public ApiResponse<AlbumDetailResponse> getAlbumById(Long id) {
        AlbumDetailResponse album = photoAlbumMapper.selectAlbumById(id);
        if (album == null) {
            return ApiResponse.error(404, "相册不存在");
        }
        return ApiResponse.success(album);
    }

    @Override
    @Transactional
    public ApiResponse<String> addAlbum(AddAlbumRequest request) {
        PhotoAlbum album = new PhotoAlbum();
        album.setAlbumName(request.getAlbumName());
        album.setDescription(request.getDescription());
        album.setAlbumCover(UrlNormalizeUtil.stripUrlPrefix(request.getAlbumCover()));
        album.setSortOrder(0);
        album.setIsVisible(true);
        photoAlbumMapper.insertAlbum(album);
        return ApiResponse.success("相册创建成功");
    }

    @Override
    @Transactional
    public ApiResponse<String> updateAlbum(UpdateAlbumRequest request) {
        PhotoAlbum album = new PhotoAlbum();
        album.setId(request.getId());
        album.setAlbumName(request.getAlbumName());
        album.setDescription(request.getDescription());
        album.setAlbumCover(UrlNormalizeUtil.stripUrlPrefix(request.getAlbumCover()));
        album.setSortOrder(request.getSortOrder());
        album.setIsVisible(request.getIsVisible());
        photoAlbumMapper.updateAlbum(album);
        return ApiResponse.success("相册更新成功");
    }

    @Override
    @Transactional
    public ApiResponse<String> deleteAlbum(Long id) {
        photoAlbumMapper.deleteAlbum(id);
        return ApiResponse.success("相册删除成功");
    }

    @Override
    @Transactional
    public ApiResponse<String> addImageToAlbum(AddImageToAlbumRequest request) {
        photoAlbumMapper.insertAlbumImage(request.getAlbumId(), request.getImageId());
        return ApiResponse.success("图片已添加到相册");
    }

    @Override
    @Transactional
    public ApiResponse<String> removeImageFromAlbum(RemoveImageFromAlbumRequest request) {
        photoAlbumMapper.deleteAlbumImage(request.getAlbumId(), request.getImageId());
        return ApiResponse.success("图片已从相册移除");
    }
}
