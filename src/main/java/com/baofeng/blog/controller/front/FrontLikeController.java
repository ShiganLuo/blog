package com.baofeng.blog.controller.front;


import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.service.LikeService;
import com.baofeng.blog.vo.front.FrontLikeVO.*;

@RestController
@RequestMapping("/api/front/likes")
@RequiredArgsConstructor
@Validated
public class FrontLikeController {

    private final LikeService likeService;

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
