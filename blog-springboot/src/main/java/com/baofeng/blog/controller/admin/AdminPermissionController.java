package com.baofeng.blog.controller.admin;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.admin.AdminPermissionDTO.*;
import com.baofeng.blog.service.PermissionService;

import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;

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
    @PreAuthorize("@perm.has('system:role:edit')")
    public ApiResponse<String> AssignPermissionForRole(@RequestBody @Validated AssignPermissionRequest assignPermissionRequest) {
        return permissionService.assignPermissionForRole(assignPermissionRequest);
    }

    @PostMapping("/addNewPermission")
    @PreAuthorize("@perm.has('system:role:edit')")
    public ApiResponse<String> AddNewPermission(@RequestBody @Validated AddNewPermissionRequest addNewPermissionRequest) {
        return permissionService.addNewPermission(addNewPermissionRequest);
    }

    @GetMapping("/getAuthRole/{userId}")
    @PreAuthorize("@perm.has('system:user:edit')")
    public ApiResponse<AuthRoleResponse> getAuthRole(@PathVariable("userId") Long userId) {
        return permissionService.getAuthRole(userId);
    }
}
