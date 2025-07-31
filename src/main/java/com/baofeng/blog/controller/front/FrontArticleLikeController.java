package com.baofeng.blog.controller.front;


import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.service.ArticleLikeService;
import com.baofeng.blog.vo.front.FrontArticleLikeVO.*;

@RestController
@RequestMapping("/api/front/likes")
@RequiredArgsConstructor
@Validated
public class FrontArticleLikeController {

    private final ArticleLikeService articleLikeService;

    @PostMapping("/addLike")
    public ApiResponse<String> addLike(@RequestBody LikeRequest request) {
        try {
            Long article_id = request.article_id();
            Long user_id = request.user_id();
            Boolean success = articleLikeService.addLike(article_id, user_id);
            if ( success ) {
                return ApiResponse.success("点赞成功");
            } else {
                return ApiResponse.error(400, "点赞失败，用户不存在或文章不存在");
            }
        } catch (Exception e) {
            return ApiResponse.error(400, "点赞失败"+e.getMessage());
        }
    }

    @PostMapping("/deleteLike")
    public ApiResponse<String> deleteLike(@RequestBody LikeRequest request) {
        try {
            Long article_id = request.article_id();
            Long user_id = request.user_id();
            Boolean success = articleLikeService.deleteLike(article_id, user_id);
            if ( success ) {
                return ApiResponse.success("取消点赞成功");
            } else {
                return ApiResponse.error(400, "取消点赞失败，没有该记录");
            }
        } catch ( Exception e) {
            return ApiResponse.error(400, "取消点赞失败" + e.getMessage());
        }
    }

    @PostMapping("/getIsLikeByArticleAndUserId")
    public ApiResponse<Boolean> getIsLikeByArticleAndUserId(@RequestBody LikeRequest request) {
        try {
            Long articleId = request.article_id();
            Long userId = request.user_id();
            Boolean successOrNot = articleLikeService.getIsLikeByArticleAndUserId(articleId, userId);
            return ApiResponse.success(successOrNot);
        } catch ( Exception e ) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

}
