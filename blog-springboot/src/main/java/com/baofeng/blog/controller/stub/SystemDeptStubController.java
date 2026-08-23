package com.baofeng.blog.controller.stub;

import com.baofeng.blog.dto.ApiResponse;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/system/dept")
public class SystemDeptStubController {

    @GetMapping("/list")
    public ApiResponse<List<Map<String, Object>>> list(@RequestParam Map<String, Object> params) {
        return ApiResponse.success(List.of());
    }

    @GetMapping("/list/exclude/{deptId}")
    public ApiResponse<List<Map<String, Object>>> listExclude(@PathVariable Long deptId) {
        return ApiResponse.success(List.of());
    }

    @GetMapping("/{deptId}")
    public ApiResponse<Map<String, Object>> getInfo(@PathVariable Long deptId) {
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

    @DeleteMapping("/{deptId}")
    public ApiResponse<String> remove(@PathVariable Long deptId) {
        return ApiResponse.success("操作成功");
    }
}
