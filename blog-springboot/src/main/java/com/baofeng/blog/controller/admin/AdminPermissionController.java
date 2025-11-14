package com.baofeng.blog.controller.admin;

import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.admin.AdminPermissionVO.AssignPermissionRequest;
import com.baofeng.blog.service.PermissionService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/permission")
@RequiredArgsConstructor
public class AdminPermissionController {
    private final PermissionService permissionService;

    /**
     * 为角色分配权限
     * @param assignPermissionRequest
     * @return
     */
    @PostMapping("/AssignPermission")
    public ApiResponse<String> AssignPermissionForRole(@RequestBody AssignPermissionRequest assignPermissionRequest) {
        return permissionService.AssignPermissionForRole(assignPermissionRequest);
    }
}
