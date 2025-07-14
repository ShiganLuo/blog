package com.baofeng.blog.controller.front;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import com.baofeng.blog.service.CommentService;
import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.front.FrontCommentVO.CreateCommentRequest;

@RestController
@RequestMapping("/api/front/comments")
@RequiredArgsConstructor
@Validated

public class FrontCommentController {

    private final CommentService commentService;

    @PostMapping("/addComment")
    public ApiResponse<String> addComment(@RequestBody CreateCommentRequest createCommentRequest) {
        try {
            Boolean success = commentService.CreateComment(createCommentRequest);

            if (success) {
                return ApiResponse.success("创建成功");
            } else {
                return ApiResponse.error(400, "创建失败");
            }

        } catch (Exception e) {
            return ApiResponse.error(400, "创建失败"+e.getMessage());
        }
    }

    
}
