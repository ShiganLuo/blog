import request from '@/utils/http'

// 角色管理（与后端 SystemRoleController /api/system/role 一一对应）
export class RoleService {
  // 角色列表（含权限数/用户数）
  static listRole(query: { roleName?: string; pageNum?: number; pageSize?: number }) {
    return request.get<any>({
      url: '/system/role/list',
      params: query
    })
  }

  // 角色详情（含已勾选权限id）
  static getRole(roleId: number) {
    return request.get<any>({
      url: '/system/role/' + roleId
    })
  }

  // 全量权限树
  static permissionTree() {
    return request.get<any>({
      url: '/system/role/permissionTree'
    })
  }

  // 新增角色（可同时勾选权限）
  static addRole(data: { roleName: string; roleDesc?: string; permissionIds?: number[] }) {
    return request.post<any>({
      url: '/system/role',
      data
    })
  }

  // 编辑角色（权限整体替换）
  static updateRole(data: { id?: number; roleName: string; roleDesc?: string; permissionIds?: number[] }) {
    return request.put<any>({
      url: '/system/role',
      data
    })
  }

  // 删除角色
  static delRole(roleId: number) {
    return request.del<any>({
      url: '/system/role/' + roleId
    })
  }
}
