package com.baofeng.blog.controller.front;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.front.FrontLikeDTO.*;
import com.baofeng.blog.service.LikeService;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/front/likes")
@Validated
public class FrontLikeController {

    private final LikeService likeService;

    public FrontLikeController (
        LikeService likeService
    ) {
        this.likeService = likeService;
    }

    @PostMapping("/addLike")
    public ApiResponse<String> addLike(@Validated @RequestBody LikeRequest request) {
        return likeService.addLikeByLikeRequest(request);
    }

    @PostMapping("/deleteLike")
    public ApiResponse<String> deleteLike(@Validated @RequestBody LikeRequest request) {
        return likeService.deleteLikeByLikeRequest(request);
    }

    @PostMapping("/getIsLike")
    public ApiResponse<Boolean> getIsLikeByArticleAndUserId(@Validated @RequestBody LikeRequest request) {
        return likeService.getIsLikeByLikeRequest(request);
    }

}
