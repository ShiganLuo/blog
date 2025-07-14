package com.baofeng.blog.vo.front;

public class FrontCommentVO {

    public record CreateCommentRequest(
        Long from_id,
        String content,
        Long for_id,
        Integer type,
        Long author_id
    ) {}
    
}
