package com.baofeng.blog.service.impl;


import com.baofeng.blog.mapper.CommentMapper;
import com.baofeng.blog.service.CommentService;
import com.baofeng.blog.entity.Comment;
import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.front.FrontCommentVO.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
  private final CommentMapper commentMapper;

  @Override
  public ApiResponse<String> CreateComment(CreateCommentRequest createCommentRequest) {
        String type = createCommentRequest.type();
        Comment comment = new Comment();
        comment.setFromId(createCommentRequest.from_id());
        comment.setContent(createCommentRequest.content());
        comment.setForId(createCommentRequest.for_id());
        comment.setType(type);
        comment.setAuthorId(createCommentRequest.author_id());
        Integer rowUpdated = commentMapper.insertComment(comment);

        return rowUpdated > 0
          ? ApiResponse.success("创建成功")
          : ApiResponse.error(400, "创建失败");

  }

  @Override
  public ApiResponse<Integer> getCommentTotal(CommentTotalRequest commentTotalRequest) {
    Long forId = commentTotalRequest.for_id();
    String type = commentTotalRequest.type();
    Integer counts = commentMapper.getCommentTotal(forId, type);

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
    // 参数校验
    int pageNum = request.current() != null ? request.current() : 1;
    int pageSize = request.size() != null ? request.size() : 10;
    // 开启分页
    PageHelper.startPage(pageNum, pageSize);
    List<CommentResponse> list = commentMapper.getCommentPage(request);
    // 获取分页信息
    PageInfo<CommentResponse> pageInfo = new PageInfo<>(list);
    // 封装返回结果
    CommentPageResponse response = new CommentPageResponse();
    response.setTotal(pageInfo.getTotal());    // 总记录数
    response.setList(pageInfo.getList());      // 当前页数据

    return ApiResponse.success(response);
  }

  @Override
  public ApiResponse<String> deleteCommentById(Long id) {
    int rowUpdated = commentMapper.deleteCommentById(id);

    return rowUpdated > 0
      ? ApiResponse.success("评论删除成功")
      : ApiResponse.error(400, "评论删除失败");
  }
} 