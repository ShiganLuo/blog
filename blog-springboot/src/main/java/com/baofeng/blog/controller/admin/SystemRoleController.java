package com.baofeng.blog.controller.admin;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.admin.AdminRoleDTO.PermissionNode;
import com.baofeng.blog.dto.admin.AdminRoleDTO.RoleDetail;
import com.baofeng.blog.dto.admin.AdminRoleDTO.RoleSaveRequest;
import com.baofeng.blog.service.RoleService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/** 角色管理（真实现，替换原 SystemRoleStubController，URL 保持 /api/system/role 不变） */
@RestController
@RequestMapping("/api/system/role")
public class SystemRoleController {

    private final RoleService roleService;

    public SystemRoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/list")
    @PreAuthorize("@perm.has('system:role:list')")
    public ApiResponse<Map<String, Object>> list(@RequestParam(required = false) String roleName,
                                                 @RequestParam(defaultValue = "1") Integer pageNum,
                                                 @RequestParam(defaultValue = "10") Integer pageSize) {
        return roleService.listRoles(roleName, pageNum, pageSize);
    }

    @GetMapping("/permissionTree")
    @PreAuthorize("@perm.has('system:role:list')")
    public ApiResponse<List<PermissionNode>> permissionTree() {
        return roleService.permissionTree();
    }

    @GetMapping("/{roleId}")
    @PreAuthorize("@perm.has('system:role:list')")
    public ApiResponse<RoleDetail> getRole(@PathVariable Long roleId) {
        return roleService.getRole(roleId);
    }

    @PostMapping
    @PreAuthorize("@perm.has('system:role:add')")
    public ApiResponse<String> add(@RequestBody @Validated RoleSaveRequest request) {
        return roleService.addRole(request);
    }

    @PutMapping
    @PreAuthorize("@perm.has('system:role:edit')")
    public ApiResponse<String> update(@RequestBody @Validated RoleSaveRequest request) {
        return roleService.updateRole(request);
    }

    @DeleteMapping("/{roleId}")
    @PreAuthorize("@perm.has('system:role:remove')")
    public ApiResponse<String> remove(@PathVariable Long roleId) {
        return roleService.deleteRole(roleId);
    }
}
