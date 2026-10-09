package com.baofeng.blog.controller.stub;

import com.baofeng.blog.dto.ApiResponse;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
public class AuthStubController {

    // getInfo 硬编码返回 *:*:* 的地雷端点已删除（登录响应本身携带真实 permissions，无调用方依赖此接口）

    @PostMapping("/api/logout")
    public ApiResponse<String> logout() {
        return ApiResponse.success("操作成功");
    }
}
