package com.baofeng.blog.controller.stub;

import com.baofeng.blog.dto.ApiResponse;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
public class SystemUserStubController {

    @PostMapping("/api/system/user")
    public ApiResponse<String> add(@RequestBody Map<String, Object> data) {
        return ApiResponse.success("操作成功");
    }

    @PutMapping("/api/system/user")
    public ApiResponse<String> update(@RequestBody Map<String, Object> data) {
        return ApiResponse.success("操作成功");
    }

    @PutMapping("/api/system/user/changeStatus")
    public ApiResponse<String> changeStatus(@RequestBody Map<String, Object> data) {
        return ApiResponse.success("操作成功");
    }

    @GetMapping("/api/system/user/deptTree")
    public ApiResponse<List<Map<String, Object>>> deptTree() {
        return ApiResponse.success(List.of());
    }
}
