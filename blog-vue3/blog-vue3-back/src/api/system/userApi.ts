import {
  EditProfileAvatarResult,
  LoginUserResult,
  UserResult,
  UserListPageResult,
  DeptOptionListResult,
  AuthRolesResult
} from '@/types/system/user'
import { BaseResult, CodeMsgResult } from '@/types/axios'
import request from '@/utils/http'
import { parseStrEmpty } from '@/utils/utils'

// 用户
export class UserService {
  // 查询用户信息列表
  static listUser(data: any) {
    return request.post<UserListPageResult>({
      url: '/admin/users/getUsersList',
      data: data
    })
  }

  // 查询用户信息详细
  static getUser(userId: any) {
    return request.get<UserResult>({
      url: '/admin/users/getUserInfoById/' + parseStrEmpty(userId)
    })
  }

  // 新增用户信息
  static addUser(data: any) {
    return request.post({
      url: '/system/user',
      data: data
    })
  }

  // 修改用户信息
  static updateUser(data: any) {
    return request.post({
      url: '/admin/users/updateUserInfo',
      data: data
    })
  }

  // 删除用户信息
  static deleteUser(userId: any) {
    return request.del({
      url: '/admin/users/deleteUser/' + userId
    })
  }

  // 用户状态修改
  static changeUserStatus(data: any) {
    return request.put({
      url: '/system/user/changeStatus',
      data: data
    })
  }

  // 重置用户密码
  static resetUserPwd(data: any) {
    return request.post({
      url: '/admin/users/passwordUpdate',
      data: data
    })
  }

  // 导出用户信息列表
  static exportExcel(data: any) {
    return request.post({
      url: 'system/user/export',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      },
      responseType: 'blob',
      data: data
    })
  }

  // 修改用户信息
  static editUserInfo(data: object) {
    return request.put<BaseResult>({
      url: '/system/user',
      data
    })
  }

  // 获取个人信息
  static getProfile() {
    return request.get<LoginUserResult>({
      url: '/admin/users/getUserInfoByToken'
    })
  }

  // 修改个人信息
  static editProfile(data: object) {
    return request.post<string>({
      url: '/admin/users/updateUserInfo',
      data
    })
  }

  // 修改个人信息密码
  static editProfilePwd(data: object) {
    return request.post<BaseResult>({
      url: '/admin/users/passwordUpdate',
      data
    })
  }

  // 修改个人信息头像
  static editProfileAvatar(data: object) {
    return request.post<EditProfileAvatarResult>({
      url: '/system/user/profile/avatar',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      data
    })
  }

  // 查询授权角色
  static getAuthRole(userId: any) {
    return request.get<AuthRolesResult>({
      url: '/admin/permission/getAuthRole/' + userId
    })
  }

  // 保存授权角色
  static updateAuthRole(data: any) {
    return request.post({
      url: '/admin/users/updateUserRoles',
      data: data
    })
  }

  // 查询部门下拉树结构
  static deptTreeSelect() {
    return request.get<DeptOptionListResult>({
      url: '/system/user/deptTree'
    })
  }

  // 下载Excel模板
  static importTemplate() {
    return request.post({
      url: '/system/user/importTemplate',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      responseType: 'blob'
    })
  }
}
