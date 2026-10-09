package com.baofeng.blog.service.impl;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.admin.AdminRoleDTO.PermissionNode;
import com.baofeng.blog.dto.admin.AdminRoleDTO.RoleDetail;
import com.baofeng.blog.dto.admin.AdminRoleDTO.RoleListRow;
import com.baofeng.blog.dto.admin.AdminRoleDTO.RoleSaveRequest;
import com.baofeng.blog.entity.Permission;
import com.baofeng.blog.entity.Role;
import com.baofeng.blog.enums.ResultCodeEnum;
import com.baofeng.blog.mapper.PermissionMapper;
import com.baofeng.blog.mapper.RoleMapper;
import com.baofeng.blog.service.RoleService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    /** 内置超级管理员角色名 */
    private static final String BUILTIN_ADMIN = "ADMIN";

    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;
    private static final Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);

    public RoleServiceImpl(RoleMapper roleMapper, PermissionMapper permissionMapper) {
        this.roleMapper = roleMapper;
        this.permissionMapper = permissionMapper;
    }

    @Override
    public ApiResponse<Map<String, Object>> listRoles(String roleName, Integer pageNum, Integer pageSize) {
        List<RoleListRow> all = roleMapper.selectRoleList();
        if (roleName != null && !roleName.isBlank()) {
            String kw = roleName.trim();
            all = all.stream()
                .filter(r -> r.getRoleName() != null && r.getRoleName().contains(kw))
                .collect(Collectors.toList());
        }
        int total = all.size();
        int page = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int size = pageSize == null || pageSize < 1 ? 10 : pageSize;
        int from = Math.min((page - 1) * size, total);
        int to = Math.min(from + size, total);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("list", all.subList(from, to));
        result.put("total", total);
        return ApiResponse.success(result);
    }

    @Override
    public ApiResponse<RoleDetail> getRole(Long roleId) {
        Role role = roleMapper.selectRoleById(roleId);
        if (role == null) {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST, "角色不存在");
        }
        List<Long> permissionIds = permissionMapper.getPermissionIdsByRoleId(roleId);
        return ApiResponse.success(new RoleDetail(
            role.getId(), role.getRoleName(), role.getRoleDesc(), role.getCreatedAt(), permissionIds));
    }

    @Override
    public ApiResponse<List<PermissionNode>> permissionTree() {
        List<Permission> permissions = permissionMapper.selectAllPermissions();
        Map<Long, PermissionNode> nodeMap = new LinkedHashMap<>();
        for (Permission p : permissions) {
            nodeMap.put(p.getId(), new PermissionNode(p.getId(), p.getName(), p.getPermission(), p.getType(), new ArrayList<>()));
        }
        List<PermissionNode> roots = new ArrayList<>();
        for (Permission p : permissions) {
            PermissionNode node = nodeMap.get(p.getId());
            if (p.getParentId() == null) {
                roots.add(node);
            } else {
                PermissionNode parent = nodeMap.get(p.getParentId());
                if (parent != null) {
                    parent.children().add(node);
                } else {
                    roots.add(node);
                }
            }
        }
        return ApiResponse.success(roots);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<String> addRole(RoleSaveRequest request) {
        String roleName = request.roleName().trim();
        if (roleMapper.selectRoleByRoleName(roleName) != null) {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST, "角色名称已存在");
        }
        Role role = new Role();
        role.setRoleName(roleName);
        role.setRoleDesc(request.roleDesc());
        int rows = roleMapper.insertRole(role);
        if (rows == 0) {
            return ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR, "角色创建失败");
        }
        savePermissions(role.getId(), request.permissionIds());
        logger.info("角色创建成功: {}", roleName);
        return ApiResponse.success("角色创建成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<String> updateRole(RoleSaveRequest request) {
        if (request.id() == null) {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST, "缺少角色ID");
        }
        Role existing = roleMapper.selectRoleById(request.id());
        if (existing == null) {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST, "角色不存在");
        }
        String newName = request.roleName().trim();
        boolean isAdminRole = BUILTIN_ADMIN.equals(existing.getRoleName());
        if (isAdminRole && !BUILTIN_ADMIN.equals(newName)) {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST, "内置角色名称不可修改");
        }
        Role dup = roleMapper.selectRoleByRoleName(newName);
        if (dup != null && !dup.getId().equals(request.id())) {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST, "角色名称已存在");
        }
        if (isAdminRole && (request.permissionIds() == null || request.permissionIds().isEmpty())) {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST, "超级管理员角色不能清空权限");
        }
        Role role = new Role();
        role.setId(request.id());
        role.setRoleName(newName);
        role.setRoleDesc(request.roleDesc());
        int rows = roleMapper.updateRole(role);
        if (rows == 0) {
            return ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR, "角色更新失败");
        }
        savePermissions(request.id(), request.permissionIds());
        return ApiResponse.success("角色更新成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<String> deleteRole(Long roleId) {
        Role existing = roleMapper.selectRoleById(roleId);
        if (existing == null) {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST, "角色不存在");
        }
        if (BUILTIN_ADMIN.equals(existing.getRoleName())) {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST, "内置角色不可删除");
        }
        // user_roles.role_id 外键为 NO ACTION，先清理角色的用户关联；role_permissions 两端 CASCADE
        roleMapper.deleteUserRolesByRoleId(roleId);
        int rows = roleMapper.deleteRoleById(roleId);
        if (rows == 0) {
            return ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR, "角色删除失败");
        }
        logger.info("角色删除成功: {} ({})", existing.getRoleName(), roleId);
        return ApiResponse.success("角色删除成功");
    }

    /** 权限整体替换（先删后插，事务内） */
    private void savePermissions(Long roleId, List<Long> permissionIds) {
        permissionMapper.deleteRolePermissions(roleId);
        if (permissionIds != null && !permissionIds.isEmpty()) {
            permissionMapper.insertRolePermissionsBatch(roleId, permissionIds);
        }
    }
}
