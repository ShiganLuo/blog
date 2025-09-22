package com.baofeng.blog.vo.front;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime expires; /** `accessToken`的过期时间（格式'xxxx/xx/xx xx:xx:xx'） */
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
