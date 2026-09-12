package com.baofeng.blog.dto.front;

import com.baofeng.blog.common.annotation.MinioFile;
import com.baofeng.blog.common.annotation.MinioScan;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

public class FrontUserDTO {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @MinioScan(maxDepth = 2)
    public static class FrontLoginResponseVO {
        private String accessToken; // 短期有效
        private String refreshToken; //长期有效

        @JsonUnwrapped
        private User user;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class User {
            private Long id;
            @MinioFile
            private String avatar;
            private String username;
            private String nickname;
            private List<String> roles;
        }
    }
    public static record FrontUpdateUserInfoRequest(
        Long userId,
        String username,
        String nickname,
        String avatar,
        String roles,
        Integer gender,
        String bio
    ) {}

    /** 前台修改密码请求 */
    public static record FrontUpdatePasswordRequest(
        Long userId,
        String oldPassword,
        String newPassword
    ) {}

    /** 前台用户统计数据 */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FrontUserStatsResponse {
        private Long articleCount;
        private Long commentCount;
        private Long talkCount;
        private Long likeCount;
    }

    /** 前台用户活动记录 */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FrontUserActivityResponse {
        private List<RecentComment> recentComments;
        private List<RecentTalk> recentTalks;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class RecentComment {
            private Long commentId;
            private String content;
            private String articleTitle;
            private Long articleId;
            private LocalDateTime createdAt;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class RecentTalk {
            private Long talkId;
            private String content;
            private LocalDateTime createdAt;
            private Integer likes;
        }
    }
}
