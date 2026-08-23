package com.baofeng.blog.controller.stub;

import com.baofeng.blog.dto.ApiResponse;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/monitor/logininfor")
public class MonitorLogininforStubController {

    @GetMapping("/list")
    public ApiResponse<Map<String, Object>> list(@RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("rows", List.of());
        result.put("total", 0);
        return ApiResponse.success(result);
    }

    @DeleteMapping("/{infoIds}")
    public ApiResponse<String> remove(@PathVariable String infoIds) {
        return ApiResponse.success("操作成功");
    }

    @DeleteMapping("/clean")
    public ApiResponse<String> clean() {
        return ApiResponse.success("操作成功");
    }

    @PutMapping("/unlock/{userName}")
    public ApiResponse<String> unlock(@PathVariable String userName) {
        return ApiResponse.success("操作成功");
    }
}
