package com.baofeng.blog.vo.admin;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
public class AdminUserAuthVO {

    // 登录请求
    public record LoginRequest(
        @NotBlank(message = "账号不能为空")
        String username,
        
        @NotBlank(message = "密码不能为空")
        String password
    ) {}

    // 注册请求
    public record RegisterRequest(
        @NotBlank(message = "用户名不能为空")
        @Size(min = 5, max = 16, message = "用户名长度5-20个字符")
        String username,

        @NotBlank(message = "密码不能为空")
        @Pattern(regexp = "^(?![0-9]+$)(?![a-z]+$)(?![A-Z]+$)(?!([^(0-9a-zA-Z)]|[()])+$)(?!^.*[\u4E00-\u9FA5].*$)([^(0-9a-zA-Z)]|[()]|[a-z]|[A-Z]|[0-9]){6,18}$",
                 message = "密码格式应为6-18位数字、字母、符号的任意两种组合")
        String password
    ) {}
    @Data
    public static class  refreshTokenResponse {
        private String accessToken;
        private String refreshToken;
        private LocalDateTime expires;
        
    }
    @Data
    public static class userPageRequest {
        private Integer current = 1;    // 当前页码
        private Integer size = 10;      // 每页大小
    }
    
    @Data
    public static class userPageResponse {
        private List<userPageVO> list;      // 数据列表
        private int total;             // 总记录数
    }
    
    @Data
    public static class userPageVO {
        private String username;
        private String nickName;
        private String avatarUrl;
        private Role role;
        public enum Role {
            USER, ADMIN
        }
            // 数据库字段 created_at
        private LocalDateTime createdAt;
        
        // 数据库字段 updated_at
        private LocalDateTime updatedAt;
        // ... 其他需要返回的用户字段
    }


} 