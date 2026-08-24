package com.baofeng.blog.service.impl;


import com.baofeng.blog.mapper.CommentMapper;
import com.baofeng.blog.mapper.LikeMapper;
import com.baofeng.blog.service.CommentService;
import com.baofeng.blog.common.util.CommentConvertUtil;
import com.baofeng.blog.common.util.UrlNormalizeUtil;
import com.baofeng.blog.common.util.minio.MinioUtil;
import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.admin.AdminCommentDTO.AdminCommentPageRequest;
import com.baofeng.blog.dto.admin.AdminCommentDTO.AdminCommentResult;
import com.baofeng.blog.dto.admin.AdminCommentDTO.AdminCommentStatusUpateRequest;
import com.baofeng.blog.dto.admin.AdminCommentDTO.AdminCommentPageResponse;
import com.baofeng.blog.dto.admin.AdminTalkDTO.AdminTalkPageRequest;
import com.baofeng.blog.dto.admin.AdminTalkDTO.AdminTalkPageResponse;
import com.baofeng.blog.dto.admin.AdminTalkDTO.AdminTalkResult;
import com.baofeng.blog.dto.admin.AdminTalkDTO.AdminTalkSaveRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.baofeng.blog.dto.front.FrontCommentDTO.*;
import com.baofeng.blog.entity.Comment;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

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
  private final MinioUtil minioUtil;

  public CommentServiceImpl (
    CommentMapper commentMapper,
    LikeMapper likeMapper,
    MinioUtil minioUtil
  ) {
    this.commentMapper = commentMapper;
    this.likeMapper = likeMapper;
    this.minioUtil = minioUtil;
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
        comment.setStatus(true); // 默认已审核
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

    ObjectMapper objectMapper = new ObjectMapper();
    if (Objects.nonNull(list) && !list.isEmpty()) {
        for (MessageResponse message : list) {
          // 解析说说图片
          if (message.getTag() != null && !message.getTag().isEmpty()) {
            try {
              List<String> imgList = objectMapper.readValue(message.getTag(), new TypeReference<List<String>>() {});
              imgList.replaceAll(url -> minioUtil.getFullUrl(url));
              message.setTalkImgList(imgList);
            } catch (Exception e) {
              message.setTalkImgList(List.of());
            }
          } else {
            message.setTalkImgList(List.of());
          }
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

  // ==================== 后台说说管理 ====================

  @Override
  public ApiResponse<AdminTalkPageResponse> getAdminTalkPage(AdminTalkPageRequest request) {
    int pageNum = request.getCurrent() != null ? request.getCurrent() : 1;
    int pageSize = request.getSize() != null ? request.getSize() : 10;
    PageHelper.startPage(pageNum, pageSize);

    List<AdminTalkResult> list = commentMapper.selectAdminTalksByCondition(request);

    // 解析 images JSON 为 imgs 数组
    ObjectMapper mapper = new ObjectMapper();
    for (AdminTalkResult talk : list) {
      if (talk.getImages() != null && !talk.getImages().isEmpty()) {
        try {
          List<String> imgList = mapper.readValue(talk.getImages(), new TypeReference<List<String>>() {});
          imgList.replaceAll(url -> minioUtil.getFullUrl(url));
          talk.setImgs(imgList);
        } catch (Exception e) {
          talk.setImgs(List.of());
        }
      } else {
        talk.setImgs(List.of());
      }
    }

    PageInfo<AdminTalkResult> pageInfo = new PageInfo<>(list);
    AdminTalkPageResponse response = new AdminTalkPageResponse();
    response.setTotal(pageInfo.getTotal());
    response.setList(pageInfo.getList());
    return ApiResponse.success(response);
  }

  @Override
  public ApiResponse<AdminTalkResult> getAdminTalkById(Long id) {
    AdminTalkResult talk = commentMapper.selectAdminTalkById(id);
    if (talk == null) {
      return ApiResponse.error(404, "说说不存在");
    }
    // 解析 images JSON
    ObjectMapper mapper = new ObjectMapper();
    if (talk.getImages() != null && !talk.getImages().isEmpty()) {
      try {
        List<String> imgList = mapper.readValue(talk.getImages(), new TypeReference<List<String>>() {});
        imgList.replaceAll(url -> minioUtil.getFullUrl(url));
        talk.setImgs(imgList);
      } catch (Exception e) {
        talk.setImgs(List.of());
      }
    } else {
      talk.setImgs(List.of());
    }
    return ApiResponse.success(talk);
  }

  @Override
  public ApiResponse<String> saveOrUpdateAdminTalk(AdminTalkSaveRequest request) {
    if (request.getContent() == null || request.getContent().trim().isEmpty()) {
      return ApiResponse.error(400, "说说内容不能为空");
    }

    Comment comment = new Comment();
    comment.setContent(request.getContent());
    comment.setStatus(request.getStatus() != null ? request.getStatus() == 1 : true);
    comment.setIsTop(request.getIsTop() != null && request.getIsTop() == 1);
    // 图片 URL 存为相对路径
    String images = request.getImages();
    if (images != null && !images.isEmpty()) {
      try {
        ObjectMapper mapper = new ObjectMapper();
        List<String> urlList = mapper.readValue(images, new TypeReference<List<String>>() {});
        urlList.replaceAll(UrlNormalizeUtil::stripUrlPrefix);
        images = mapper.writeValueAsString(urlList);
      } catch (Exception ignored) {}
    }
    comment.setTag(images);

    if (request.getId() != null) {
      // 修改
      comment.setId(request.getId());
      int rows = commentMapper.updateTalk(comment);
      return rows > 0
        ? ApiResponse.success("修改说说成功")
        : ApiResponse.error(400, "修改说说失败");
    } else {
      // 新增 - 需要设置 userId，从安全上下文获取
      // 管理员创建说说，默认使用当前登录用户
      comment.setUserId(1L); // TODO: 从 SecurityContext 获取当前用户ID
      int rows = commentMapper.insertTalk(comment);
      return rows > 0
        ? ApiResponse.success("发布说说成功")
        : ApiResponse.error(400, "发布说说失败");
    }
  }

  @Override
  public ApiResponse<String> deleteAdminTalks(List<Long> ids) {
    if (ids == null || ids.isEmpty()) {
      return ApiResponse.error(400, "ID列表不能为空");
    }
    int rows = commentMapper.deleteCommentsByIds(new HashSet<>(ids));
    return rows > 0
      ? ApiResponse.success("删除说说成功")
      : ApiResponse.error(400, "删除说说失败");
  }

} 