package com.baofeng.blog.dto.front;

import com.baofeng.blog.common.annotation.MinioFile;
import com.baofeng.blog.common.annotation.MinioScan;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
        String roles
    ) {}
}
