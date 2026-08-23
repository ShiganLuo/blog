package com.baofeng.blog.controller.stub;

import com.baofeng.blog.dto.ApiResponse;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
public class ToolGenStubController {

    @GetMapping("/api/tool/gen/list")
    public ApiResponse<Map<String, Object>> list(@RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("rows", List.of());
        result.put("total", 0);
        return ApiResponse.success(result);
    }

    @GetMapping("/api/tool/gen/db/list")
    public ApiResponse<Map<String, Object>> dbList(@RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("rows", List.of());
        result.put("total", 0);
        return ApiResponse.success(result);
    }

    @PostMapping("/api/tool/gen/importTable")
    public ApiResponse<String> importTable(@RequestParam Map<String, Object> params) {
        return ApiResponse.success("操作成功");
    }

    @PostMapping("/api/tool/gen/createTable")
    public ApiResponse<String> createTable(@RequestBody Map<String, Object> data) {
        return ApiResponse.success("操作成功");
    }

    @GetMapping("/api/tool/gen/batchGenCode")
    public void batchGenCode(@RequestParam String tables, @RequestParam Map<String, Object> params) {
        // 返回空
    }
}
