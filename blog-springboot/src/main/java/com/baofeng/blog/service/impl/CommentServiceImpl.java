package com.baofeng.blog.service.impl;


import com.baofeng.blog.mapper.CommentMapper;
import com.baofeng.blog.mapper.LikeMapper;
import com.baofeng.blog.service.CommentService;
import com.baofeng.blog.common.util.CommentConvertUtil;
import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.front.FrontCommentDTO.*;
import com.baofeng.blog.entity.Comment;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
  private final CommentMapper commentMapper;
  private final LikeMapper likeMapper;

  @Override
  public ApiResponse<String> CreateComment(CreateCommentRequest createCommentRequest) {
        String type = createCommentRequest.type();
        Comment comment = new Comment();
        comment.setFromId(createCommentRequest.from_id());
        comment.setContent(createCommentRequest.content());
        comment.setToId(createCommentRequest.to_id());
        comment.setForId(createCommentRequest.for_id());
        comment.setType(type);
        comment.setAuthorId(createCommentRequest.author_id());
        comment.setRootId(createCommentRequest.root_id());
        Integer rowUpdated = commentMapper.insertComment(comment);

        return rowUpdated > 0
          ? ApiResponse.success("创建成功")
          : ApiResponse.error(400, "创建失败");

  }

  @Override
  public ApiResponse<Integer> getCommentTotal(Long rootId) {
    Integer counts = commentMapper.getCommentTotal(rootId);
    return ApiResponse.success(counts);
  }

  @Override
  public ApiResponse<NotifyPageResponse> getNotifyPage(NotifyPageRequest request) {
    // 参数校验
    int pageNum = request.current() != null ? request.current() : 1;
    int pageSize = request.size() != null ? request.size() : 10;
    // 开启分页
    PageHelper.startPage(pageNum, pageSize);
    List<NotifyResponse> list = commentMapper.getNotifyPage(request);
    // 获取分页信息
    PageInfo<NotifyResponse> pageInfo = new PageInfo<>(list);
    // 封装返回结果
    NotifyPageResponse response = new NotifyPageResponse();
    response.setTotal(pageInfo.getTotal());    // 总记录数
    response.setList(pageInfo.getList());      // 当前页数据

    return ApiResponse.success(response);
  }

  @Override
  public ApiResponse<CommentPageResponse> getCommentPage(CommentPageRequest request) {
    int pageNum = request.current() != null ? request.current() : 1;
    int pageSize = request.size() != null ? request.size() : 10;

    PageHelper.startPage(pageNum, pageSize);
    List<ArticleCommentResponse> list = commentMapper.getCommentsByCondition(request);

    
    if (Objects.nonNull(list) && !list.isEmpty()) {
      for (ArticleCommentResponse comment : list) {
          // 如果用户id为null，表明是游客
          if (request.user_id() != null) {
            // 一条评论不可能既是一级评论又是子级评论
            Long postLikeId = likeMapper.selectIdByLikeRequestAndStatus(
              comment.getId(), 
              "post", 
              request.user_id(), 
              true
            );
            Long commentLikeId = likeMapper.selectIdByLikeRequestAndStatus(
                comment.getId(), 
                "comment", 
                request.user_id(), 
                true
            );
            if (Objects.nonNull(postLikeId) || Objects.nonNull(commentLikeId)) {
                comment.setIs_like(true);
            } else {
                comment.setIs_like(false);
            }
          } else {
            comment.setIs_like(false);
          }

      }
    }   
    

    PageInfo<ArticleCommentResponse> pageInfo = new PageInfo<>(list);

    CommentPageResponse response = new CommentPageResponse();
    response.setTotal(pageInfo.getTotal());
    List<CommentResponse> commentTree = CommentConvertUtil.buildCommentTree(pageInfo.getList());
    response.setList(commentTree);

    return ApiResponse.success(response);
  }

  @Override
  public ApiResponse<String> deleteCommentById(Long id) {
    if (id == null) {
      return ApiResponse.error(400, "评论ID不能为空");
    }

    // 存储所有需要删除的评论ID，包括原始评论
    Set<Long> commentIdsToDelete = new HashSet<>();
    
    // 使用队列进行广度优先遍历（BFS）
    Queue<Long> queue = new LinkedList<>();
    queue.add(id);

    // 遍历评论树，收集所有子孙评论ID
    while (!queue.isEmpty()) {
      Long currentId = queue.poll();
      commentIdsToDelete.add(currentId);

      List<Comment> childComments = commentMapper.selectChildComment(currentId);
      if (childComments != null && !childComments.isEmpty()) {
        for (Comment child : childComments) {
          queue.add(child.getId());
        }
      }
    }

    // 批量删除所有收集到的评论
    if (!commentIdsToDelete.isEmpty()) {
      // 假设你有一个Mapper方法可以批量删除
      int rowsAffected = commentMapper.deleteCommentsByIds(commentIdsToDelete);
      
      return rowsAffected > 0
          ? ApiResponse.success("评论及其所有子评论删除成功")
          : ApiResponse.error(400, "评论删除失败");
    }
    
    return ApiResponse.error(400, "评论删除失败");
  }

  @Override
  public ApiResponse<List<Comment>> getChildComment(Long commentId) {
    List<Comment> comments = commentMapper.selectChildComment(commentId);
    return ApiResponse.success(comments);
  }

  @Override
  public ApiResponse<MessagePageResponse> getAllMessage(MessageTalkPageRequest request) {
    int pageNum = request.current() != null ? request.current() : 1;
    int pageSize = request.size() != null ? request.size() : 10;
    PageHelper.startPage(pageNum, pageSize);

    List<MessageResponse> list = commentMapper.selectAllMessageTalk(request);

    if (Objects.nonNull(list) && !list.isEmpty()) {
        for (MessageResponse message : list) {
          // 如果用户id为null，表明是游客
          if (request.user_id() != null) {
            Long likeId = likeMapper.selectIdByLikeRequestAndStatus(
                message.getId(), 
                "message", 
                message.getFrom_id(), 
                true
            );
            
            if (Objects.nonNull(likeId)) {
                message.setIs_like(true);;
            } else {
                message.setIs_like(false);
            }
         } else {
            message.setIs_like(false);
          }
        }   
    }

    PageInfo<MessageResponse> pageInfo = new PageInfo<>(list);

    MessagePageResponse response = new MessagePageResponse();
    response.setTotal(pageInfo.getTotal());
    response.setList(pageInfo.getList());

    return ApiResponse.success(response);
  }
} 