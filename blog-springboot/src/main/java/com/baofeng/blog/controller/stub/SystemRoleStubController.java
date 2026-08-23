package com.baofeng.blog.controller.stub;

import com.baofeng.blog.dto.ApiResponse;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/system/role")
public class SystemRoleStubController {

    @GetMapping("/list")
    public ApiResponse<Map<String, Object>> list(@RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("rows", List.of());
        result.put("total", 0);
        return ApiResponse.success(result);
    }

    @GetMapping("/{roleId}")
    public ApiResponse<Map<String, Object>> getInfo(@PathVariable Long roleId) {
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

    @PutMapping("/dataScope")
    public ApiResponse<String> dataScope(@RequestBody Map<String, Object> data) {
        return ApiResponse.success("操作成功");
    }

    @PutMapping("/changeStatus")
    public ApiResponse<String> changeStatus(@RequestBody Map<String, Object> data) {
        return ApiResponse.success("操作成功");
    }

    @DeleteMapping("/{roleId}")
    public ApiResponse<String> remove(@PathVariable Long roleId) {
        return ApiResponse.success("操作成功");
    }

    @GetMapping("/authUser/allocatedList")
    public ApiResponse<Map<String, Object>> allocatedList(@RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("rows", List.of());
        result.put("total", 0);
        return ApiResponse.success(result);
    }

    @GetMapping("/authUser/unallocatedList")
    public ApiResponse<Map<String, Object>> unallocatedList(@RequestParam Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("rows", List.of());
        result.put("total", 0);
        return ApiResponse.success(result);
    }

    @PutMapping("/authUser/cancel")
    public ApiResponse<String> authUserCancel(@RequestBody Map<String, Object> data) {
        return ApiResponse.success("操作成功");
    }

    @PutMapping("/authUser/cancelAll")
    public ApiResponse<String> authUserCancelAll(@RequestParam Long roleId, @RequestParam String userIds) {
        return ApiResponse.success("操作成功");
    }

    @PutMapping("/authUser/selectAll")
    public ApiResponse<String> authUserSelectAll(@RequestParam Long roleId, @RequestParam String userIds) {
        return ApiResponse.success("操作成功");
    }

    @GetMapping("/optionselect")
    public ApiResponse<List<Map<String, Object>>> optionselect() {
        return ApiResponse.success(List.of());
    }

    @GetMapping("/deptTree/{roleId}")
    public ApiResponse<Map<String, Object>> deptTree(@PathVariable Long roleId) {
        Map<String, Object> result = new HashMap<>();
        result.put("checkedKeys", List.of());
        result.put("depts", List.of());
        return ApiResponse.success(result);
    }
}
