package com.baofeng.blog.vo.common;
import jakarta.validation.constraints.*;

public class User {
    // 登录请求
    public record LoginRequest(
        @NotBlank(message = "账号不能为空")
        String username,
        
        @NotBlank(message = "密码不能为空")
        String password
    ) {}
}
