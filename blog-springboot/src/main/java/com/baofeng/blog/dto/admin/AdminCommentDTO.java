package com.baofeng.blog.dto.admin;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AdminCommentDTO {

    @Data
    public static class AdminCommentPageRequest{
        @NotNull(message = "当前页不能为空")
        @Min(value = 1, message = "当前页必须大于等于 1")
        private Integer current;

        @NotNull(message = "每页数量不能为空")
        @Min(value = 1, message = "每页数量必须大于等于 1")
        private Integer size;

        private List<String> type;
        private Long forId;
        private Integer status;
    }

    @Data
    public static class AdminCommentPageResponse {
        private long total;
        private List<AdminCommentResult> list;
    }
    
    @Data
    public static class AdminCommentResult {
        private Long id;
        private Long userId;
        private String userName;
        private Long forId;
        private String CommentContent;
        private Long replyUserId;
        private String replyUserName;
        private Long parentId;
        private String type;
        private String status;
        private Integer isDeleted;
        private Integer isReviewed;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

    }

    public static record AdminCommentStatusUpateRequest(
        @NotNull
        List<Long> ids,
        @NotNull
        Integer status
    ){}
}
