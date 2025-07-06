package com.baofeng.blog.service.impl;

import com.baofeng.blog.vo.admin.AdminCommentPageVO.CommentPageRequestVO;
import com.baofeng.blog.vo.admin.AdminCommentPageVO.CommentPageResponseVO;
import com.baofeng.blog.vo.admin.AdminCommentPageVO.CommentVO;
import com.baofeng.blog.entity.Comment;
import com.baofeng.blog.mapper.CommentMapper;
import com.baofeng.blog.service.CommentService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentMapper commentMapper;

    @Override
    public CommentPageResponseVO getCommentPage(CommentPageRequestVO request) {
        // 参数校验
        int pageNum = request.pageNum() != null ? request.pageNum() : 1;
        int pageSize = request.pageSize() != null ? request.pageSize() : 10;
        
        // 开启分页
        PageHelper.startPage(pageNum, pageSize);
        
        // 执行查询
        List<CommentVO> list = commentMapper.getCommentPage(request);
        
        // 获取分页信息
        PageInfo<CommentVO> pageInfo = new PageInfo<>(list);
        
        // 封装返回结果
        CommentPageResponseVO response = new CommentPageResponseVO();
        response.setTotal(pageInfo.getTotal());    // 总记录数
        response.setPages(pageInfo.getPages());    // 总页数
        response.setList(pageInfo.getList());      // 当前页数据
        return response;
    }

    @Override
    @Transactional
    public boolean deleteComment(Long id) {
        // 检查评论是否存在
        Comment comment = commentMapper.getCommentById(id);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }

        // 检查是否有子评论
        int childCount = commentMapper.getChildCommentCount(id);
        if (childCount > 0) {
            // 删除所有子评论
            commentMapper.deleteChildComments(id);
        }

        // 删除评论本身
        return commentMapper.deleteComment(id) > 0;
    }
} 