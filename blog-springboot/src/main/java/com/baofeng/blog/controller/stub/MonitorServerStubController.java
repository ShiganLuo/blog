package com.baofeng.blog.controller.stub;

import com.baofeng.blog.dto.ApiResponse;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/monitor/server")
public class MonitorServerStubController {

    @GetMapping
    public ApiResponse<Map<String, Object>> getInfo() {
        Map<String, Object> result = new HashMap<>();
        result.put("cpu", Map.of("cpuNum", 0, "used", 0, "sys", 0, "free", 100));
        result.put("mem", Map.of("total", 0, "used", 0, "free", 0, "usage", 0));
        result.put("jvm", Map.of("total", 0, "max", 0, "free", 0, "version", "17", "home", ""));
        result.put("sys", Map.of("osName", "Linux", "osArch", "amd64", "computerIp", "127.0.0.1", "computerName", "localhost"));
        result.put("sysFiles", List.of());
        return ApiResponse.success(result);
    }
}
