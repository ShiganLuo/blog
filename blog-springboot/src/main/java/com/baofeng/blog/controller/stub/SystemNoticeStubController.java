package com.baofeng.blog.controller.stub;

import com.baofeng.blog.dto.ApiResponse;
import org.springframework.web.bind.annotation.*;
import java.util.*;

/**
 * RuoYi 系统管理模块存根 - 通知公告
 * 返回空数据，避免前端 404
 */
@RestController
@RequestMapping("/api/system/notice")
public class SystemNoticeStubController {

    @GetMapping("/list")
    public ApiResponse<Map<String, Object>> list(@RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("rows", List.of());
        result.put("total", 0);
        return ApiResponse.success(result);
    }

    @GetMapping("/{noticeId}")
    public ApiResponse<Map<String, Object>> getInfo(@PathVariable Long noticeId) {
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

    @DeleteMapping("/{noticeId}")
    public ApiResponse<String> remove(@PathVariable Long noticeId) {
        return ApiResponse.success("操作成功");
    }
}
