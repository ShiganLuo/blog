package com.baofeng.blog.vo.front;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

public class FrontCommentVO {

    /*
     * 创建评论请求体
     */
    public record CreateCommentRequest(
        Long from_id,
        String content,
        Long for_id,
        Integer type,
        Long author_id
    ) {}

    /*
     * 获取评论总数请求体
     */
    public record CommentTotalRequest(
        Long for_id,
        Integer type
    ){}

    /*
     * 用户通知请求体
     */
    public record NotifyPageRequest(
        Integer current,
        Integer size,
        Long userId
    ){}

    /*
     * 用户通知分页响应体
     */
    @Data
    public static class NotifyPageResponse{
        private List<NotifyResponse> list;
        private Long total;

    }

    /*
     * 用户通知响应体
     */
    @Data
    public static class NotifyResponse {
        private Long id;
        private Boolean isView;
        private String message;
        private String type;
        private Long to_id;
    }

    /*
     * 评论分页请求体
     */
    public record CommentPageRequest(
        Integer current,
        Integer size,
        String order // new or hot(time or views)
    ) {}

    /**
     * 评论分页响应体
     */
    @Data
    public static class CommentPageResponse {
        private List<CommentResponse> list;
        private Long total;
    }
    /**
     * 评论响应体
     */
    @Data
    public static class CommentResponse {
        private Long id;
        private Long from_id;
        private String from_name;
        private String from_avatar;
        private String content;
        private LocalDateTime createdAt;
        private Long thumbs_up;
        private Boolean is_like;
        private String ipAdress;
        private Boolean showApplyInput;
    }

}
