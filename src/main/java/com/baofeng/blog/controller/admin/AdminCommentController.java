package com.baofeng.blog.controller.admin;

import com.baofeng.blog.service.CommentService;
import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.admin.AdminCommentPageVO.CommentPageRequestVO;
import com.baofeng.blog.vo.admin.AdminCommentPageVO.CommentPageResponseVO;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/api/admin/comments")
@RequiredArgsConstructor
@Validated
public class AdminCommentController {
    
    private final CommentService commentService;

    /**
     * 删除评论
     * @param id 评论ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteComment(@PathVariable Long id) {
        try {
            boolean success = commentService.deleteComment(id);
            if (success) {
                return ApiResponse.success(null);
            } else {
                return ApiResponse.error(500, "删除评论失败");
            }
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "删除失败：" + e.getMessage());
        }
    }

    /**
     * 分页查询评论列表
     * @param request 分页查询参数
     * @return 分页结果
     */
    @PostMapping("/list")
    public ApiResponse<CommentPageResponseVO> getCommentPage(@RequestBody CommentPageRequestVO request) {
        // 参数校验
        if (request == null) {
            return ApiResponse.error(400, "请求参数不能为空");
        }
        if (request.pageNum() != null && request.pageNum() < 1) {
            return ApiResponse.error(400, "页码必须大于0");
        }
        if (request.pageSize() != null && request.pageSize() < 1) {
            return ApiResponse.error(400, "每页显示条数必须大于0");
        }
        
        try {
            return ApiResponse.success(commentService.getCommentPage(request));
        } catch (Exception e) {
            return ApiResponse.error(500, "查询失败：" + e.getMessage());
        }
    }
} 