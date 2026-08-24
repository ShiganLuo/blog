package com.baofeng.blog.mapper;

import com.baofeng.blog.entity.PhotoAlbum;
import com.baofeng.blog.dto.front.PhotoAlbumDTO.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PhotoAlbumMapper {

    List<AlbumListResponse> selectAllAlbums();

    AlbumDetailResponse selectAlbumById(@Param("id") Long id);

    int insertAlbum(PhotoAlbum album);

    int updateAlbum(PhotoAlbum album);

    int deleteAlbum(@Param("id") Long id);

    int insertAlbumImage(@Param("albumId") Long albumId, @Param("imageId") Long imageId);

    int deleteAlbumImage(@Param("albumId") Long albumId, @Param("imageId") Long imageId);

    int countAlbumImages(@Param("albumId") Long albumId);
}
