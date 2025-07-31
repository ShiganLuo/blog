package com.baofeng.blog.controller.admin;

import com.baofeng.blog.service.CommentService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/api/admin/comments")
@RequiredArgsConstructor
@Validated
public class AdminCommentController {
    
    private final CommentService commentService;


} 