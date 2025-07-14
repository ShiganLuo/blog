package com.baofeng.blog.service.impl;


import com.baofeng.blog.mapper.CommentMapper;
import com.baofeng.blog.service.CommentService;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentMapper commentMapper;

   
} 