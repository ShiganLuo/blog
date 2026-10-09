package com.baofeng.blog.component;

import com.baofeng.blog.entity.Role;
import com.baofeng.blog.mapper.PermissionMapper;
import com.baofeng.blog.mapper.RoleMapper;
import com.baofeng.blog.mapper.UserMapper;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SpEL 权限校验入口，配合 @EnableMethodSecurity 使用：
 *   @PreAuthorize("@perm.has('system:user:remove')")
 * 校验链：当前登录用户 → user_roles → role_permissions → permissions.permission
 */
@Component("perm")
public class PermissionChecker {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;

    public PermissionChecker(UserMapper userMapper, RoleMapper roleMapper, PermissionMapper permissionMapper) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.permissionMapper = permissionMapper;
    }

    /** 是否拥有指定权限（支持 *:*:* 通配） */
    public boolean has(String permission) {
        List<String> permissions = currentPermissions();
        return permissions.contains("*:*:*") || permissions.contains(permission);
    }

    /** 是否拥有其中任一权限 */
    public boolean hasAny(String... permissions) {
        List<String> current = currentPermissions();
        if (current.contains("*:*:*")) {
            return true;
        }
        for (String p : permissions) {
            if (current.contains(p)) {
                return true;
            }
        }
        return false;
    }

    private List<String> currentPermissions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return List.of();
        }
        String username = authentication.getName();
        if (username == null || "anonymousUser".equals(username)) {
            return List.of();
        }
        Long userId = userMapper.getIdByUsername(username);
        if (userId == null) {
            return List.of();
        }
        List<Long> roleIds = roleMapper.selectRolesByUserId(userId).stream()
            .map(Role::getId)
            .collect(Collectors.toList());
        if (roleIds.isEmpty()) {
            return List.of();
        }
        return permissionMapper.selectPermissionsByRoleIds(roleIds);
    }
}
