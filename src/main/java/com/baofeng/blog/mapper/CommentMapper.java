package com.baofeng.blog.mapper;


import org.apache.ibatis.annotations.Mapper;

import com.baofeng.blog.entity.Comment;

@Mapper
public interface CommentMapper {

    /**
     * 创建评论
     * @param commentRequest
     * @return Comment
     */
    Integer insertComment(Comment comment);

} 