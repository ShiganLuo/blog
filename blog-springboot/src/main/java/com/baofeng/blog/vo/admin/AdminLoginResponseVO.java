package com.baofeng.blog.vo.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.List;
/**
 * LoginResponse 直接将 token 定义为 String
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminLoginResponseVO {
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