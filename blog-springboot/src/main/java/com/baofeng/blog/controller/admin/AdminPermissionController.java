package com.baofeng.blog.controller.admin;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.admin.AdminPermissionDTO.*;
import com.baofeng.blog.service.PermissionService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/permission")
public class AdminPermissionController {
    private final PermissionService permissionService;
    public AdminPermissionController (
        PermissionService permissionService
    ) {
        this.permissionService = permissionService;
    }

    /**
     * 为角色分配权限
     * @param assignPermissionRequest
     * @return
     */
    @PostMapping("/AssignPermission")
    public ApiResponse<String> AssignPermissionForRole(@RequestBody AssignPermissionRequest assignPermissionRequest) {
        return permissionService.assignPermissionForRole(assignPermissionRequest);
    }

    @PostMapping("/addNewPermission")
    public ApiResponse<String> AddNewPermission(@RequestBody AddNewPermissionRequest addNewPermissionRequest) {
        return permissionService.addNewPermission(addNewPermissionRequest);
    }

    @GetMapping("/getAuthRole/{userId}")
    public ApiResponse<AuthRoleResponse> getAuthRole(@PathVariable("userId") Long userId) {
        return permissionService.getAuthRole(userId);
    }
}
