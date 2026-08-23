package com.baofeng.blog.controller.stub;

import com.baofeng.blog.dto.ApiResponse;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/system/post")
public class SystemPostStubController {

    @GetMapping("/list")
    public ApiResponse<Map<String, Object>> list(@RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("rows", List.of());
        result.put("total", 0);
        return ApiResponse.success(result);
    }

    @GetMapping("/{postId}")
    public ApiResponse<Map<String, Object>> getInfo(@PathVariable Long postId) {
        return ApiResponse.success(new HashMap<>());
    }

    @PostMapping
    public ApiResponse<String> add(@RequestBody Map<String, Object> data) {
        return ApiResponse.success("操作成功");
    }

    @PutMapping
    public ApiResponse<String> update(@RequestBody Map<String, Object> data) {
        return ApiResponse.success("操作成功");
    }

    @DeleteMapping("/{postId}")
    public ApiResponse<String> remove(@PathVariable Long postId) {
        return ApiResponse.success("操作成功");
    }
}
