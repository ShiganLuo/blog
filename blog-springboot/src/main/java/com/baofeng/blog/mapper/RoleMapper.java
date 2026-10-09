package com.baofeng.blog.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baofeng.blog.entity.Role;
import java.util.List;

@Mapper
public interface RoleMapper {
    /**
     * 根据用户ID查询角色列表
     * @param userId
     * @return
     */
    List<Role> selectRolesByUserId(Long userId);

    /**
     * 插入新角色
     * @param role
     * @return
     */
    int insertRole(Role role);

    /**
     * 根据ID删除角色
     * @param roleId
     * @return
     */
    int deleteRoleById(Long roleId);

    /**
     * 为用户分配角色
     * @param userId
     * @param roleId
     * @return
     */
    int insertUserRole(Long userId, Long roleId);

    /**
     * 根据用户ID删除用户角色关联
     * @param userId
     * @return
     */
    int deleteUserRolesByUserId(Long userId);

    /**
     * 根据角色名查询角色类型是否存在
     * @param roleName
     * @return
     */
    Role selectRoleByRoleName(String roleName);

    /**
    * 查询所有角色
    * @return
    */
    List<Role> getAllRoles();

    /**
     * 角色列表（含权限数/用户数）
     * @return
     */
    List<com.baofeng.blog.dto.admin.AdminRoleDTO.RoleListRow> selectRoleList();

    /**
     * 根据ID查询角色
     * @param roleId
     * @return
     */
    Role selectRoleById(Long roleId);

    /**
     * 更新角色
     * @param role
     * @return
     */
    int updateRole(Role role);

    /**
     * 删除角色的用户关联（user_roles.role_id 外键为 NO ACTION）
     * @param roleId
     * @return
     */
    int deleteUserRolesByRoleId(Long roleId);
} 