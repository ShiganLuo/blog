package com.baofeng.blog.mapper;

import com.baofeng.blog.entity.Permission;
import com.baofeng.blog.entity.RolePermission;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface PermissionMapper {
    /**
     * 根据用户角色id列表批量获取用户权限
     * @param roleId
     * @return
     */
    List<String> selectPermissionsByRoleIds(List<Long> roleIds);

    /**
     * 新建权限
     * @param permission
     * @return
     */
    Permission insertPermission(Permission permission);

    /**
     * 根据权限名查询权限
     * @param permissionName
     * @return
     */
    Permission selectPermissionByName(String permissionName);

    /**
     * 分配权限
     * @param rolePermission
     * @return
     */
    int insertRolePermission(RolePermission rolePermission);

    /**
     * 根据角色id和权限id查询权限是否已经分配
     * @param roleId
     * @param permissionId
     * @return
     */
    RolePermission selectRolePermission(Long roleId, Long permissionId);
}
