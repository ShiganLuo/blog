package com.baofeng.blog.service;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.admin.AdminRoleDTO.PermissionNode;
import com.baofeng.blog.dto.admin.AdminRoleDTO.RoleDetail;
import com.baofeng.blog.dto.admin.AdminRoleDTO.RoleSaveRequest;

import java.util.List;
import java.util.Map;

public interface RoleService {
    /** 角色列表（含权限数/用户数，支持名称过滤与分页） */
    ApiResponse<Map<String, Object>> listRoles(String roleName, Integer pageNum, Integer pageSize);

    /** 角色详情 + 已勾选权限id */
    ApiResponse<RoleDetail> getRole(Long roleId);

    /** 全量权限树 */
    ApiResponse<List<PermissionNode>> permissionTree();

    /** 新建角色（可同时勾选权限） */
    ApiResponse<String> addRole(RoleSaveRequest request);

    /** 编辑角色（权限整体替换） */
    ApiResponse<String> updateRole(RoleSaveRequest request);

    /** 删除角色（清理用户关联，内置角色保护） */
    ApiResponse<String> deleteRole(Long roleId);
}
