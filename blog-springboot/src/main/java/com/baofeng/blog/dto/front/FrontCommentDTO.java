package com.baofeng.blog.dto.front;

import java.time.LocalDateTime;
import java.util.List;

import com.baofeng.blog.common.annotation.MinioFile;
import com.baofeng.blog.common.annotation.MinioScan;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
public class FrontCommentDTO {

    /*
     * 创建评论请求体
     */
    public record CreateCommentRequest(
        @NotNull Long from_id,
        @NotBlank String content,
        Long for_id,
        Long to_id,
        @NotBlank String type,
        Long author_id,
        Long root_id,
        String tag
    ) {}


    /*
     * 用户通知请求体
     */
    public static record NotifyPageRequest(
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
    public static record CommentPageRequest(

        @NotNull(message = "当前页不能为空")
        @Min(value = 1, message = "当前页必须大于等于 1")
        Integer current,

        @NotNull(message = "每页数量不能为空")
        @Min(value = 1, message = "每页数量必须大于等于 1")
        Integer size,

        List<String> type,
        Long for_id,

        // order 可选，但如果写了必须是 new 或 hot
        @Pattern(regexp = "^(new|hot)?$", message = "order 参数只能是 'new' 或 'hot'")
        String order,

        Long rootId,
        Long user_id

    ) {}

    /**
     * 评论分页响应体
     */
    @Data
    @MinioScan(maxDepth = 2)
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
        @MinioFile
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
        @MinioFile
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

    public static record MessageTalkPageRequest(
        @NotNull(message = "当前页不能为空")
        @Min(value = 1, message = "当前页必须大于等于 1")
        Integer current,

        @NotNull(message = "每页数量不能为空")
        @Min(value = 1, message = "当前页必须大于等于 1")
        Integer size,

        @NotBlank
        String type,
        Long user_id
    ){    }
    
    @Data
    @MinioScan(maxDepth = 2)
    public static class MessagePageResponse {
        private Long total;
        private List<MessageResponse> list;
    }

    @Data
    public static class MessageResponse {
        private Long id;
        @MinioFile
        private String avatar;
        private Long from_id;
        private String nick_name;
        private String content;
        private LocalDateTime createdAt;
        private Long thumbs_up;
        private Boolean is_like;
    }
}
