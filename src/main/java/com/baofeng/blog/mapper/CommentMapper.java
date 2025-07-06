package com.baofeng.blog.mapper;

import com.baofeng.blog.vo.admin.AdminCommentPageVO.CommentVO;
import com.baofeng.blog.entity.Comment;
import com.baofeng.blog.vo.admin.AdminCommentPageVO.CommentPageRequestVO;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface CommentMapper {
    /**
     * 分页查询评论列表
     * @param request 分页查询参数
     * @return 评论列表
     */
    List<CommentVO> getCommentPage(CommentPageRequestVO request);

    /**
     * 根据ID查询评论
     * @param id 评论ID
     * @return 评论信息
     */
    Comment getCommentById(Long id);

    /**
     * 删除评论
     * @param id 评论ID
     * @return 影响的行数
     */
    int deleteComment(Long id);

    /**
     * 删除指定评论的所有子评论
     * @param parentId 父评论ID
     * @return 影响的行数
     */
    int deleteChildComments(Long parentId);

    /**
     * 获取评论的子评论数量
     * @param id 评论ID
     * @return 子评论数量
     */
    int getChildCommentCount(Long id);
} 