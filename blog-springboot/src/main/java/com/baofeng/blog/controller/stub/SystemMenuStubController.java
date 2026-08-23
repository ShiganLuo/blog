package com.baofeng.blog.controller.stub;

import com.baofeng.blog.dto.ApiResponse;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/system/menu")
public class SystemMenuStubController {

    @GetMapping("/list")
    public ApiResponse<List<Map<String, Object>>> list(@RequestParam Map<String, Object> params) {
        return ApiResponse.success(List.of());
    }

    @GetMapping("/{menuId}")
    public ApiResponse<Map<String, Object>> getInfo(@PathVariable Long menuId) {
        return ApiResponse.success(new HashMap<>());
    }

    @GetMapping("/treeselect")
    public ApiResponse<List<Map<String, Object>>> treeselect(@RequestParam Map<String, Object> params) {
        return ApiResponse.success(List.of());
    }

    @GetMapping("/roleMenuTreeselect/{roleId}")
    public ApiResponse<Map<String, Object>> roleMenuTreeselect(@PathVariable Long roleId) {
        Map<String, Object> result = new HashMap<>();
        result.put("checkedKeys", List.of());
        result.put("menus", List.of());
        return ApiResponse.success(result);
    }

    @PostMapping
    public ApiResponse<String> add(@RequestBody Map<String, Object> data) {
        return ApiResponse.success("操作成功");
    }

    @PutMapping
    public ApiResponse<String> update(@RequestBody Map<String, Object> data) {
        return ApiResponse.success("操作成功");
    }

    @DeleteMapping("/{menuId}")
    public ApiResponse<String> remove(@PathVariable Long menuId) {
        return ApiResponse.success("操作成功");
    }
}
