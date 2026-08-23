package com.baofeng.blog.controller.admin;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.admin.AdminTalkDTO.AdminTalkPageRequest;
import com.baofeng.blog.dto.admin.AdminTalkDTO.AdminTalkPageResponse;
import com.baofeng.blog.dto.admin.AdminTalkDTO.AdminTalkResult;
import com.baofeng.blog.dto.admin.AdminTalkDTO.AdminTalkSaveRequest;
import com.baofeng.blog.dto.admin.AdminTalkDTO.AdminTalkDeleteRequest;
import com.baofeng.blog.service.CommentService;

import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/api/blog/talk/admin/talks")
public class AdminTalkController {

    private final CommentService commentService;

    public AdminTalkController(CommentService commentService) {
        this.commentService = commentService;
    }

    /** 获取后台说说列表 */
    @GetMapping
    public ApiResponse<AdminTalkPageResponse> listTalks(AdminTalkPageRequest request) {
        return commentService.getAdminTalkPage(request);
    }

    /** 根据ID获取说说 */
    @GetMapping("/{id}")
    public ApiResponse<AdminTalkResult> getTalkById(@PathVariable Long id) {
        return commentService.getAdminTalkById(id);
    }

    /** 新增或修改说说 */
    @PostMapping
    public ApiResponse<String> saveOrUpdateTalk(@RequestBody @Validated AdminTalkSaveRequest request) {
        return commentService.saveOrUpdateAdminTalk(request);
    }

    /** 删除说说 */
    @DeleteMapping
    public ApiResponse<String> deleteTalks(@RequestBody @Validated AdminTalkDeleteRequest request) {
        return commentService.deleteAdminTalks(request.getIds());
    }
}
