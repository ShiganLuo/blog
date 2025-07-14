package com.baofeng.blog.service.impl;


import com.baofeng.blog.mapper.CommentMapper;
import com.baofeng.blog.service.CommentService;
import com.baofeng.blog.entity.Comment;
import com.baofeng.blog.vo.front.FrontCommentVO.CreateCommentRequest;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentMapper commentMapper;

   @Override
   public Boolean CreateComment(CreateCommentRequest createCommentRequest) {
        Integer type = createCommentRequest.type();
        Comment comment = new Comment();
        comment.setFromId(createCommentRequest.from_id());
        comment.setContent(createCommentRequest.content());
        comment.setForId(createCommentRequest.for_id());
        comment.setType(type);
        comment.setAuthorId(createCommentRequest.author_id());
        Integer rowUpdated = commentMapper.insertComment(comment);

        return rowUpdated > 0;

   }
} 