package com.baofeng.blog.service;

import com.baofeng.blog.vo.front.FrontCommentVO.CreateCommentRequest;

public interface CommentService {

    Boolean CreateComment(CreateCommentRequest createCommentRequest);


} 