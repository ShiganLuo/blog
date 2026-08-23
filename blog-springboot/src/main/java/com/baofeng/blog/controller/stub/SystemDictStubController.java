package com.baofeng.blog.controller.stub;

import com.baofeng.blog.dto.ApiResponse;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
public class SystemDictStubController {

    @GetMapping("/api/system/dict/type/list")
    public ApiResponse<Map<String, Object>> listType(@RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("rows", List.of());
        result.put("total", 0);
        return ApiResponse.success(result);
    }

    @GetMapping("/api/system/dict/type/{dictId}")
    public ApiResponse<Map<String, Object>> getType(@PathVariable Long dictId) {
        return ApiResponse.success(new HashMap<>());
    }

    @PostMapping("/api/system/dict/type")
    public ApiResponse<String> addType(@RequestBody Map<String, Object> data) {
        return ApiResponse.success("操作成功");
    }

    @PutMapping("/api/system/dict/type")
    public ApiResponse<String> updateType(@RequestBody Map<String, Object> data) {
        return ApiResponse.success("操作成功");
    }

    @DeleteMapping("/api/system/dict/type/{dictId}")
    public ApiResponse<String> removeType(@PathVariable Long dictId) {
        return ApiResponse.success("操作成功");
    }

    @GetMapping("/api/system/dict/type/optionselect")
    public ApiResponse<List<Map<String, Object>>> optionselect() {
        return ApiResponse.success(List.of());
    }

    @DeleteMapping("/api/system/dict/type/refreshCache")
    public ApiResponse<String> refreshTypeCache() {
        return ApiResponse.success("操作成功");
    }

    @GetMapping("/api/system/dict/data/{dictCode}")
    public ApiResponse<Map<String, Object>> getData(@PathVariable Long dictCode) {
        return ApiResponse.success(new HashMap<>());
    }

    @PostMapping("/api/system/dict/data")
    public ApiResponse<String> addData(@RequestBody Map<String, Object> data) {
        return ApiResponse.success("操作成功");
    }

    @PutMapping("/api/system/dict/data")
    public ApiResponse<String> updateData(@RequestBody Map<String, Object> data) {
        return ApiResponse.success("操作成功");
    }

    @DeleteMapping("/api/system/dict/data/{dictCode}")
    public ApiResponse<String> removeData(@PathVariable Long dictCode) {
        return ApiResponse.success("操作成功");
    }
}
