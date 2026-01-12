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
        @NotNull 
        Long userId,
        @NotBlank 
        String content,
        Long forId,
        Long replyUserId,
        @NotBlank 
        String type,
        Long authorId,
        Long rootId,
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
        private Boolean isViewed; // 待修改
        private String message;
        private String type;
        private Long replyUserId;
    }

    /*
     * 评论分页请求体
     */
    public static record FrontCommentPageRequest(

        @NotNull(message = "当前页不能为空")
        @Min(value = 1, message = "当前页必须大于等于 1")
        Integer current,

        @NotNull(message = "每页数量不能为空")
        @Min(value = 1, message = "每页数量必须大于等于 1")
        Integer size,

        List<String> type,
        Long forId,

        // order 可选，但如果写了必须是 new 或 hot
        @Pattern(regexp = "^(new|hot)?$", message = "order 参数只能是 'new' 或 'hot'")
        String order,

        Long rootId,
        Long userId

    ) {}

    /**
     * 评论分页响应体
     */
    @Data
    @MinioScan(maxDepth = 2)
    public static class FrontCommentPageResponse {
        private List<FrontCommentResponse> list;
        private Long total;
    }
    /**
     * 评论响应体
     */
    @Data
    public static class FrontCommentResponse {
        private Long id;
        private Long userId;
        private String userName;
        @MinioFile
        private String userAvatar;
        private String replyUserName;
        private Long forId;
        private String content;
        private LocalDateTime createdAt;
        private Long likes;
        private String ipAdress;
        private Boolean isLiked;
        private List<FrontCommentResponse> childComments;
    }

    @Data
    public static class FrontArticleCommentResponse {
        private Long id;
        private Long userId;
        private String userName;
        @MinioFile
        private String userAvatar;
        private String replyUserName;
        private Long forId;
        private String type;
        private String content;
        private LocalDateTime createdAt;
        private Long likes;
        private String ipAdress;
        private Boolean isLiked;
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
        Long userId
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
        private Long userId;
        private String nickname;
        private String content;
        private LocalDateTime createdAt;
        private Long likes;
        private Boolean isLiked;
    }
}
