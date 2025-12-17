package com.baofeng.blog.dto.admin;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

public class AdminUserAuthDTO {

    // 注册请求
    public static record RegisterRequest(
        @NotBlank(message = "用户名不能为空")
        @Size(min = 5, max = 16, message = "用户名长度5-20个字符")
        String username,

        @NotBlank(message = "密码不能为空")
        @Pattern(regexp = "^(?![0-9]+$)(?![a-z]+$)(?![A-Z]+$)(?!([^(0-9a-zA-Z)]|[()])+$)(?!^.*[\u4E00-\u9FA5].*$)([^(0-9a-zA-Z)]|[()]|[a-z]|[A-Z]|[0-9]){6,18}$",
                 message = "密码格式应为6-18位数字、字母、符号的任意两种组合")
        String password
    ) {}

    @Data
    public static class UserPageRequest {
        private Integer current = 1;    // 当前页码
        private Integer size = 10;      // 每页大小
    }
    
    @Data
    public static class UserPageResponse {
        private List<UserPageVO> list;      // 数据列表
        private int total;             // 总记录数
    }
    
    @Data
    public static class UserPageVO {
        private Long userId;
        private String userName;
        private String nickName;
        private String email;
        private String phoneNumber;
        private Integer sex;
        private String status;
        private String avatarUrl;
        private String role;
        private LocalDateTime createdAt;
        private LocalDateTime loginDate;
        // 数据库字段 updated_at
        private LocalDateTime updatedAt;
        // ... 其他需要返回的用户字段
    }

    public static record UpdateUserRoleRequest(
        Long userId,
        List<String> roles
    ) { 
        public UpdateUserRoleRequest {
            if (userId == null) {
                throw new IllegalArgumentException("userId不能为空");
            }
            if (roles == null || roles.isEmpty()) {
                throw new IllegalArgumentException("roles不能为空");
            }
        }
    }

    public static record UpdateUserInfo (
        Long id,
        String username,
        String nickName,
        String email,
        String phoneNumber,
        Integer gender
    ) {}

    public static record UpdatePasswordRequest(
        @NotNull
        String userName,
        @NotNull
        String newPassword
    ) {}

} 