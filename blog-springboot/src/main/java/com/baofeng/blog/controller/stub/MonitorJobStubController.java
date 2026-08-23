package com.baofeng.blog.controller.stub;

import com.baofeng.blog.dto.ApiResponse;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
public class MonitorJobStubController {

    @GetMapping("/api/monitor/job/list")
    public ApiResponse<Map<String, Object>> list(@RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("rows", List.of());
        result.put("total", 0);
        return ApiResponse.success(result);
    }

    @GetMapping("/api/monitor/job/{jobId}")
    public ApiResponse<Map<String, Object>> getInfo(@PathVariable Long jobId) {
        return ApiResponse.success(new HashMap<>());
    }

    @PostMapping("/api/monitor/job")
    public ApiResponse<String> add(@RequestBody Map<String, Object> data) {
        return ApiResponse.success("操作成功");
    }

    @PutMapping("/api/monitor/job")
    public ApiResponse<String> update(@RequestBody Map<String, Object> data) {
        return ApiResponse.success("操作成功");
    }

    @DeleteMapping("/api/monitor/job/{jobIds}")
    public ApiResponse<String> remove(@PathVariable String jobIds) {
        return ApiResponse.success("操作成功");
    }

    @PutMapping("/api/monitor/job/changeStatus")
    public ApiResponse<String> changeStatus(@RequestBody Map<String, Object> data) {
        return ApiResponse.success("操作成功");
    }

    @PutMapping("/api/monitor/job/run/{jobId}")
    public ApiResponse<String> run(@PathVariable Long jobId, @RequestParam String jobGroup) {
        return ApiResponse.success("操作成功");
    }

    @GetMapping("/api/monitor/jobLog/list")
    public ApiResponse<Map<String, Object>> logList(@RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("rows", List.of());
        result.put("total", 0);
        return ApiResponse.success(result);
    }

    @DeleteMapping("/api/monitor/jobLog/{jobLogIds}")
    public ApiResponse<String> logRemove(@PathVariable String jobLogIds) {
        return ApiResponse.success("操作成功");
    }

    @DeleteMapping("/api/monitor/jobLog/clean")
    public ApiResponse<String> logClean() {
        return ApiResponse.success("操作成功");
    }
}
