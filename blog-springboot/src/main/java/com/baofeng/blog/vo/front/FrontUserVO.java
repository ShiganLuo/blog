package com.baofeng.blog.vo.front;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

public class FrontUserVO {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
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
        private String avatar;
        private String username;
        private String nickname;
        private List<String> roles;
    }
} 
}
