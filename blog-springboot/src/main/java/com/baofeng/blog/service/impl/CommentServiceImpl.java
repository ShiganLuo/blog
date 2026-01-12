package com.baofeng.blog.service.impl;


import com.baofeng.blog.mapper.CommentMapper;
import com.baofeng.blog.mapper.LikeMapper;
import com.baofeng.blog.service.CommentService;
import com.baofeng.blog.common.util.CommentConvertUtil;
import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.admin.AdminCommentDTO.AdminCommentPageRequest;
import com.baofeng.blog.dto.admin.AdminCommentDTO.AdminCommentResult;
import com.baofeng.blog.dto.admin.AdminCommentDTO.AdminCommentStatusUpateRequest;
import com.baofeng.blog.dto.admin.AdminCommentDTO.AdminCommentPageResponse;
import com.baofeng.blog.dto.front.FrontCommentDTO.*;
import com.baofeng.blog.entity.Comment;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties.Admin;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Objects;


@Service
public class CommentServiceImpl implements CommentService {
  
  private final CommentMapper commentMapper;
  private final LikeMapper likeMapper;

  public CommentServiceImpl (
    CommentMapper commentMapper,
    LikeMapper likeMapper
  ) {
    this.commentMapper = commentMapper;
    this.likeMapper = likeMapper;
  }

  @Override
  public ApiResponse<String> CreateComment(CreateCommentRequest createCommentRequest) {
        String type = createCommentRequest.type();
        Comment comment = new Comment();
        comment.setUserId(createCommentRequest.userId());
        comment.setContent(createCommentRequest.content());
        comment.setReplyUserId(createCommentRequest.replyUserId());
        comment.setForId(createCommentRequest.forId());
        comment.setType(type);
        comment.setAuthorId(createCommentRequest.authorId());
        comment.setRootId(createCommentRequest.rootId());
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
  public ApiResponse<FrontCommentPageResponse> getFrontCommentPage(FrontCommentPageRequest request) {
    int pageNum = request.current() != null ? request.current() : 1;
    int pageSize = request.size() != null ? request.size() : 10;

    PageHelper.startPage(pageNum, pageSize);
    List<FrontArticleCommentResponse> list = commentMapper.getCommentsByCondition(request);

    
    if (Objects.nonNull(list) && !list.isEmpty()) {
      for (FrontArticleCommentResponse comment : list) {
          // 如果用户id为null，表明是游客
          if (request.userId() != null) {
            // 一条评论不可能既是一级评论又是子级评论
            Long postLikeId = likeMapper.selectIdByLikeRequestAndStatus(
              comment.getId(), 
              "post", 
              request.userId(), 
              true
            );
            Long commentLikeId = likeMapper.selectIdByLikeRequestAndStatus(
                comment.getId(), 
                "comment", 
                request.userId(), 
                true
            );
            if (Objects.nonNull(postLikeId) || Objects.nonNull(commentLikeId)) {
                comment.setIsLiked(true);
            } else {
                comment.setIsLiked(false);
            }
          } else {
            comment.setIsLiked(false);
          }

      }
    }   
    

    PageInfo<FrontArticleCommentResponse> pageInfo = new PageInfo<>(list);

    FrontCommentPageResponse response = new FrontCommentPageResponse();
    response.setTotal(pageInfo.getTotal());
    List<FrontCommentResponse> commentTree = CommentConvertUtil.buildCommentTree(pageInfo.getList());
    response.setList(commentTree);

    return ApiResponse.success(response);
  }

  @Override
  public ApiResponse<AdminCommentPageResponse> getAdminCommentPage(AdminCommentPageRequest request) {
    int current = request.getCurrent() != null ? request.getCurrent() : 1;
    int size = request.getSize() != null ? request.getSize() : 10;
    PageHelper.startPage(current, size);
    List<AdminCommentResult> list = commentMapper.getAdminCommentsByCondition(request);
    PageInfo<AdminCommentResult> pageInfo = new PageInfo<>(list);
    AdminCommentPageResponse response = new AdminCommentPageResponse();
    response.setTotal(pageInfo.getTotal());
    response.setList(pageInfo.getList());
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
          if (request.userId() != null) {
            Long likeId = likeMapper.selectIdByLikeRequestAndStatus(
                message.getId(), 
                "message", 
                message.getUserId(), 
                true
            );
            
            if (Objects.nonNull(likeId)) {
                message.setIsLiked(true);;
            } else {
                message.setIsLiked(false);
            }
         } else {
            message.setIsLiked(false);
          }
        }   
    }

    PageInfo<MessageResponse> pageInfo = new PageInfo<>(list);

    MessagePageResponse response = new MessagePageResponse();
    response.setTotal(pageInfo.getTotal());
    response.setList(pageInfo.getList());

    return ApiResponse.success(response);
  }

  @Override
      public ApiResponse<String> updateCommentsStatusByIds(AdminCommentStatusUpateRequest request) {
        if (request == null || request.ids() == null || request.ids().isEmpty()) {
            return ApiResponse.error(400, "评论ID列表不能为空");
        }

        int rowsAffected = commentMapper.updateCommentsStatusByIds(request);

        return rowsAffected > 0
            ? ApiResponse.success("评论状态更新成功")
            : ApiResponse.error(400, "评论状态更新失败");
      }

} 