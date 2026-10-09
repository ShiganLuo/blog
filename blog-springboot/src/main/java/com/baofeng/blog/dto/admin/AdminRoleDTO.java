package com.baofeng.blog.dto.admin;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/** 角色管理 DTO */
public class AdminRoleDTO {

    /** 角色列表行（含权限数/用户数） */
    @Data
    public static class RoleListRow {
        private Long id;
        private String roleName;
        private String roleDesc;
        private LocalDateTime createdAt;
        private Long permCount;
        private Long userCount;
    }

    /** 角色详情（含已勾选权限id） */
    public record RoleDetail(Long id, String roleName, String roleDesc, LocalDateTime createdAt,
                             List<Long> permissionIds) {}

    /** 权限树节点 */
    public record PermissionNode(Long id, String name, String permission, String type,
                                 List<PermissionNode> children) {}

    /** 新增/编辑角色入参 */
    public record RoleSaveRequest(Long id,
                                  @jakarta.validation.constraints.NotBlank String roleName,
                                  String roleDesc,
                                  List<Long> permissionIds) {}
}
