package com.baofeng.blog.controller.front;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import com.baofeng.blog.service.CommentService;
import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.front.FrontCommentVO.*;

@RestController
@RequestMapping("/api/front/comments")
@RequiredArgsConstructor
@Validated

public class FrontCommentController {

    private final CommentService commentService;

    /**
     * 添加评论
     * @param createCommentRequest
     * @return
     */
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

    /**
     * 获取评论总数
     * @param commentTotalRequest
     * @return
     */
    @PostMapping("/getCommentTotal")
    public ApiResponse<Integer> getCommentTotal(@RequestBody CommentTotalRequest commentTotalRequest) {
        try {
            Integer counts = commentService.getCommentTotal(commentTotalRequest);
            return ApiResponse.success(counts);
        } catch (Exception e) {
            return ApiResponse.error(400, "文章评论总数统计失败" + e.getMessage());
        }
    }

    /**
     * 获取评论中用户相关信息
     * @param request
     * @return
     */
    @PostMapping("/getNotifyPage")
    public ApiResponse<NotifyPageResponse> getNotifyPageInfo(@RequestBody NotifyPageRequest request) {
        try {
            NotifyPageResponse response = commentService.getNotifyPage(request);
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error(400, "获取用户通知信息失败");
        }
    }

    /**
     * 获取评论分页
     * @param request
     * @return
     */
    @PostMapping("/getCommentPage")
    public ApiResponse<CommentPageResponse> getCommentPageInfo(@RequestBody CommentPageRequest request) {
        try {
            CommentPageResponse response = commentService.getCommentPage(request);
            return ApiResponse.success(response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ApiResponse.error(400, "获取评论分页信息失败");
        }
    }

    @GetMapping("/deleteCommentById/{id}")
    public ApiResponse<String> deleteCommentById(@PathVariable Long id) {
        try {
            Boolean success = commentService.deleteCommentById(id);
            if (success) {
                return ApiResponse.success("评论删除成功");
            } else {
                return ApiResponse.error(400, "评论删除失败");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ApiResponse.error(400, "系统出行意外");
        } 
    }


    
}
