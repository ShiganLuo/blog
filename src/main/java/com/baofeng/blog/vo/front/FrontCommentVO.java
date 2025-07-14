package com.baofeng.blog.vo.front;

public class FrontCommentVO {

    public record CommentRequest(
        Long from_id,
        String from_avatar,
        String from_name,
        String content,
        Long for_id,
        Integer type,
        Long author_id
    ) {}
    
}
