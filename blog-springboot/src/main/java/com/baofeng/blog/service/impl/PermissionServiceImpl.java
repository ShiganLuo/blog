package com.baofeng.blog.service.impl;

import com.baofeng.blog.service.PermissionService;
import com.baofeng.blog.entity.Role;
import com.baofeng.blog.entity.User;
import com.baofeng.blog.entity.RolePermission;
import com.baofeng.blog.enums.ResultCodeEnum;
import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.admin.AdminPermissionDTO.AddNewPermissionRequest;
import com.baofeng.blog.dto.admin.AdminPermissionDTO.AssignPermissionRequest;
import com.baofeng.blog.dto.admin.AdminPermissionDTO.AuthRoleResponse;
import com.baofeng.blog.dto.admin.AdminPermissionDTO.RoleType;
import com.baofeng.blog.dto.admin.AdminPermissionDTO.UserInfo;
import com.baofeng.blog.entity.Permission;
import com.baofeng.blog.mapper.PermissionMapper;
import com.baofeng.blog.mapper.RoleMapper;
import com.baofeng.blog.mapper.UserMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService{
    private final PermissionMapper permissionMapper;
    private final RoleMapper roleMapper;
    private final UserMapper userMapper;
    private static final Logger logger = LoggerFactory.getLogger(PermissionService.class);
    public PermissionServiceImpl (
        PermissionMapper permissionMapper,
        RoleMapper roleMapper,
        UserMapper userMapper
    ) {
        this.permissionMapper = permissionMapper;
        this.roleMapper = roleMapper;
        this.userMapper = userMapper;
    }

    @Override
    public ApiResponse<String> assignPermissionForRole(AssignPermissionRequest assignPermissionRequest) {
        String roleName = assignPermissionRequest.roleName();
        String permissionName = assignPermissionRequest.permission();

        Role role = roleMapper.selectRoleByRoleName(roleName);
        if (role == null) {
            logger.warn("分配权限失败：角色不存在. RoleName: {}", roleName);
            return ApiResponse.error(ResultCodeEnum.NOT_FOUND, "角色 [" + roleName + "] 不存在，请先创建该角色。");
        }
        Permission permission = permissionMapper.selectPermissionByName(permissionName);
        if (permission == null) {
            logger.warn("分配权限失败：权限不存在. PermissionName: {}", permissionName);
            return ApiResponse.error(ResultCodeEnum.NOT_FOUND, "权限 [" + permissionName + "] 不存在，请先创建该权限。");
        }

        RolePermission existing = permissionMapper.selectRolePermission(role.getId(), permission.getId());
        if (existing != null) {
            logger.info("权限已分配：角色 [{}] 已拥有权限 [{}].", roleName, permissionName);
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST, "角色 [" + roleName + "] 已经拥有权限 [" + permissionName + "]，无需重复分配。");
        }

        RolePermission rolePermission = new RolePermission();
        rolePermission.setRoleId(role.getId());
        rolePermission.setPermissionId(permission.getId());

        int rowUpdated = 0;
        try {
            rowUpdated = permissionMapper.insertRolePermission(rolePermission);
        } catch (Exception e) {
            logger.error("分配权限插入数据库失败. Role: {}, Permission: {}. 错误信息: {}", 
                        roleName, permissionName, e.getMessage());
            return ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR, "数据库操作失败，分配权限操作未完成。");
        }
        
        if (rowUpdated > 0) {
            logger.info("分配权限成功：角色 [{}] 获得了权限 [{}].", roleName, permissionName);
            return ApiResponse.success("分配权限成功：角色 [" + roleName + "] 获得了权限 [" + permissionName + "]");
        } else {            
            logger.error("分配权限失败，但未捕获到异常。Role: {}, Permission: {}", roleName, permissionName);
            return ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR, "分配权限失败：数据库操作返回 0 行受影响。");
        }
    }

    @Override
    public ApiResponse<String> addNewPermission(AddNewPermissionRequest addNewPermissionRequest) {
        Permission permission = Permission.builder()
        .name(addNewPermissionRequest.name())
        .permission(addNewPermissionRequest.permission())
        .type(addNewPermissionRequest.type() != null && !addNewPermissionRequest.type().isEmpty() ? addNewPermissionRequest.type() : null)
        .path(addNewPermissionRequest.path() != null && !addNewPermissionRequest.path().isEmpty() ? addNewPermissionRequest.path() : null)
        .parentId(addNewPermissionRequest.parentId())
        .build();
        
        int rowUpdated = permissionMapper.insertPermission(permission);
        return rowUpdated > 0
            ? ApiResponse.success("权限新建成功")
            : ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR,"权限新建失败");
    }

    @Override
    public ApiResponse<AuthRoleResponse> getAuthRole(Long userId) {
        UserInfo userInfo = new UserInfo();
        User user = userMapper.selectUserById(userId);
        if (user == null) {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST,"用户不存在");
        }
        userInfo.setNickName(user.getNickName());
        userInfo.setUserName(user.getUsername());
        userInfo.setUserId(userId);
        List<String> user_roles = roleMapper.selectRolesByUserId(userId).stream()
            .map(Role::getRoleName)
            .collect(Collectors.toList());
        userInfo.setRoles(user_roles);
        List<Role> roles = roleMapper.getAllRoles();
        List<RoleType> roleTypes = roles.stream()
            .map( role -> {
                    RoleType roleType = new RoleType();
                    roleType.setRoleId(role.getId());
                    roleType.setRoleName(role.getRoleName());
                    roleType.setRoleDesc(role.getRoleDesc());
                    roleType.setCreatedAt(role.getCreatedAt());
                    roleType.setUpdatedAt(role.getUpdatedAt());
                    List<Long> permissionIds = permissionMapper.getPermissionIdsByRoleId(role.getId());
                    List<String> permissions = permissionIds.stream()
                        .map(
                            permissionId -> {
                                String permission = permissionMapper.getPermissionTagByPermissionId(permissionId);
                                return permission;
                            }
                        )
                        .collect(Collectors.toList());
                    
                    roleType.setPermissions(permissions);
                    return roleType;
                } 
            )
            .collect(Collectors.toList());

        AuthRoleResponse authRoleResponse = new AuthRoleResponse(roleTypes, userInfo);
        return ApiResponse.success(authRoleResponse);
    }

}
