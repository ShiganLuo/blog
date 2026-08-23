package com.baofeng.blog.controller.stub;

import com.baofeng.blog.dto.ApiResponse;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
public class AuthStubController {

    @GetMapping("/api/getInfo")
    public ApiResponse<Map<String, Object>> getInfo() {
        Map<String, Object> result = new HashMap<>();
        result.put("user", new HashMap<>());
        result.put("roles", List.of("admin"));
        result.put("permissions", List.of("*:*:*"));
        return ApiResponse.success(result);
    }

    @PostMapping("/api/logout")
    public ApiResponse<String> logout() {
        return ApiResponse.success("操作成功");
    }
}
