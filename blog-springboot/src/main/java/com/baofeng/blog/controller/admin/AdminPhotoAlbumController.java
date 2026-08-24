package com.baofeng.blog.controller.admin;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.front.PhotoAlbumDTO.*;
import com.baofeng.blog.service.PhotoAlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/photoAlbum")
@RequiredArgsConstructor
public class AdminPhotoAlbumController {

    private final PhotoAlbumService photoAlbumService;

    @GetMapping("/list")
    public ApiResponse<List<AlbumListResponse>> getAllAlbums() {
        return photoAlbumService.getAllAlbums();
    }

    @GetMapping("/{id}")
    public ApiResponse<AlbumDetailResponse> getAlbumById(@PathVariable Long id) {
        return photoAlbumService.getAlbumById(id);
    }

    @PostMapping("/add")
    public ApiResponse<String> addAlbum(@RequestBody @Validated AddAlbumRequest request) {
        return photoAlbumService.addAlbum(request);
    }

    @PutMapping("/update")
    public ApiResponse<String> updateAlbum(@RequestBody @Validated UpdateAlbumRequest request) {
        return photoAlbumService.updateAlbum(request);
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<String> deleteAlbum(@PathVariable Long id) {
        return photoAlbumService.deleteAlbum(id);
    }

    @PostMapping("/addImage")
    public ApiResponse<String> addImageToAlbum(@RequestBody @Validated AddImageToAlbumRequest request) {
        return photoAlbumService.addImageToAlbum(request);
    }

    @PostMapping("/removeImage")
    public ApiResponse<String> removeImageFromAlbum(@RequestBody @Validated RemoveImageFromAlbumRequest request) {
        return photoAlbumService.removeImageFromAlbum(request);
    }
}
