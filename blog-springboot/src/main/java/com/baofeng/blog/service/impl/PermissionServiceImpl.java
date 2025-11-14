package com.baofeng.blog.service.impl;

import com.baofeng.blog.service.ArticleService;
import com.baofeng.blog.service.PermissionService;
import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.admin.AdminPermissionVO.AssignPermissionRequest;
import com.baofeng.blog.entity.Role;
import com.baofeng.blog.entity.RolePermission;
import com.baofeng.blog.enums.ResultCodeEnum;
import com.baofeng.blog.entity.Permission;
import com.baofeng.blog.mapper.PermissionMapper;
import com.baofeng.blog.mapper.RoleMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PermissionServiceImpl implements PermissionService{
    private final PermissionMapper permissionMapper;
    private final RoleMapper roleMapper;
    private static final Logger logger = LoggerFactory.getLogger(PermissionService.class);
    public PermissionServiceImpl (
        PermissionMapper permissionMapper,
        RoleMapper roleMapper
    ) {
        this.permissionMapper = permissionMapper;
        this.roleMapper = roleMapper;
    }

    public ApiResponse<String> AssignPermissionForRole(AssignPermissionRequest assignPermissionRequest) {
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



}
