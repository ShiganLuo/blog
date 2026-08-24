package com.baofeng.blog.controller.front;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.front.PhotoAlbumDTO.*;
import com.baofeng.blog.service.PhotoAlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/front/photoAlbum")
@RequiredArgsConstructor
public class FrontPhotoAlbumController {

    private final PhotoAlbumService photoAlbumService;

    @GetMapping("/list")
    public ApiResponse<List<AlbumListResponse>> getAllAlbums() {
        return photoAlbumService.getAllAlbums();
    }

    @GetMapping("/{id}")
    public ApiResponse<AlbumDetailResponse> getAlbumById(@PathVariable Long id) {
        return photoAlbumService.getAlbumById(id);
    }
}
