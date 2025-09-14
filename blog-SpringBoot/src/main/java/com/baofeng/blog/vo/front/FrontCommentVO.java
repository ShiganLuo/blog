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
        Long to_id,
        String type,
        Long author_id,
        Long root_id,
        String tag
    ) {
        public CreateCommentRequest {
            if (from_id == null) {
                throw new IllegalArgumentException("from_id不能为空");
            }
            if (type == null) {
                throw new IllegalArgumentException("type不能为空");
            }
            if (content == null) {
                throw new IllegalArgumentException("content不能为空");
            }

        }
    }


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
        List<String> type,
        Long for_id,
        String order, // new or hot
        Long rootId,
        Long user_id
    ) {
        public CommentPageRequest {
            if (current == null) {
                throw new IllegalArgumentException("当前页不能为空");
            }
            if (size == null) {
                throw new IllegalArgumentException("每页数量不能为空");
            }
            if (current < 1) {
                throw new IllegalArgumentException("当前页必须大于等于 1");
            }
            if (size < 1) {
                throw new IllegalArgumentException("每页数量必须大于等于 1");
            }
            if (order != null && !order.isEmpty()) {
                if (!order.equals("new") && !order.equals("hot")) {
                    throw new IllegalArgumentException("order 参数只能是 'new' 或 'hot'");
                }
            }
        }
    }

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
        private String to_name;
        private Long for_id;
        private String content;
        private LocalDateTime createdAt;
        private Long thumbs_up;
        private String ipAdress;
        private Boolean is_like;
        private List<CommentResponse> childComments;
    }

    @Data
    public static class ArticleCommentResponse {
        private Long id;
        private Long from_id;
        private String from_name;
        private String from_avatar;
        private String to_name;
        private Long for_id;
        private String type;
        private String content;
        private LocalDateTime createdAt;
        private Long thumbs_up;
        private String ipAdress;
        private Boolean is_like;
    }

    public record MessageTalkPageRequest(
        Integer current,
        Integer size,
        String type,
        Long user_id
    ){
        public MessageTalkPageRequest {
            if (current == null) {
                throw new IllegalArgumentException("当前页不能为空");
            }
            if (size == null) {
                throw new IllegalArgumentException("每页数量不能为空");
            }
            if (type == null) {
                throw new IllegalArgumentException("类型不能为空");
            }
            if (current < 1) {
                throw new IllegalArgumentException("当前页必须大于等于 1");
            }
            if (size < 1) {
                throw new IllegalArgumentException("每页数量必须大于等于 1");
            }
        }
    }
    @Data
    public static class MessagePageResponse {
        private Long total;
        private List<MessageResponse> list;
    }

    @Data
    public static class MessageResponse {
        private Long id;
        private String avatar;
        private Long from_id;
        private String nick_name;
        private String content;
        private LocalDateTime createdAt;
        private Long thumbs_up;
        private Boolean is_like;
    }
}
