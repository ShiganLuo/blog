package com.baofeng.blog.controller.stub;

import com.baofeng.blog.dto.ApiResponse;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/monitor/cache")
public class MonitorCacheStubController {

    @GetMapping
    public ApiResponse<Map<String, Object>> getInfo() {
        Map<String, Object> result = new HashMap<>();
        result.put("info", Map.of("dbSize", 0, "used_memory_human", "0B", "uptime_in_days", "0"));
        result.put("commandStats", List.of());
        return ApiResponse.success(result);
    }

    @GetMapping("/getNames")
    public ApiResponse<List<String>> getNames() {
        return ApiResponse.success(List.of());
    }

    @GetMapping("/getKeys")
    public ApiResponse<List<String>> getKeys(@RequestParam String cacheName) {
        return ApiResponse.success(List.of());
    }

    @GetMapping("/getValue")
    public ApiResponse<Map<String, Object>> getValue(@RequestParam String cacheName, @RequestParam String cacheKey) {
        return ApiResponse.success(Map.of("cacheName", cacheName, "cacheKey", cacheKey, "cacheValue", ""));
    }

    @DeleteMapping("/clearCacheName")
    public ApiResponse<String> clearCacheName(@RequestParam String cacheName) {
        return ApiResponse.success("操作成功");
    }

    @DeleteMapping("/clearCacheKey")
    public ApiResponse<String> clearCacheKey(@RequestParam String cacheKey) {
        return ApiResponse.success("操作成功");
    }

    @DeleteMapping("/clearCacheAll")
    public ApiResponse<String> clearCacheAll() {
        return ApiResponse.success("操作成功");
    }
}
