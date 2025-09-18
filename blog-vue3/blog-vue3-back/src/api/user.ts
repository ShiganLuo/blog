import { IUser,UserLoginResponse } from '@/interface';
import { request,IResponse } from '@/utils/request';

export function fetchLogin({ username, password }) {
  return request<UserLoginResponse>({
    url: '/api/admin/users/login',
    method: 'post',
    data: { username, password },
  });
}

export interface UserInfo {
  id: number;
  username: string;
  email: string | null;
  password?: string; // 敏感信息，通常不需要
  avatarUrl: string | null;
  bio: string | null;
  nickName: string;
  role: 'USER' | 'ADMIN' | string; // 角色通常是枚举值
  status: 'ACTIVE' | 'INACTIVE' | string; // 状态通常也是枚举值
  createdAt: string;
  updatedAt: string;
  lastLogin: string | null;
  loginAttempts: number;
  emailVerified: boolean | null;
  active: boolean | null;
}
export function fetchUserInfo(id:number) {
  return request<UserInfo>({
    url: `/api/admin/users/getUserInfoById/${id}`,
    method: 'get',
  });
}

export function fetchUserList(params) {
  return request({
    url: '/user/list',
    method: 'get',
    params,
  });
}

export function fetchUserDetail(id: number): Promise<IResponse<IUser>> {
  return request({
    method: 'get',
    url: `/user/find/${id}`
  });
}

export function fetchUserPwd() {
  return request({
    url: `/user/get_pwd`,
    method: 'get',
  });
}

export function fetchUpdateUser({ id, username, status, avatar, desc }: IUser) {
  return request({
    url: `/user/update/${id}`,
    method: 'put',
    data: {
      username,
      status,
      avatar,
      desc,
    },
  });
}

export function fetchUpdatePwd({ oldpwd, newpwd }) {
  return request({
    url: `/user/update_pwd`,
    method: 'put',
    data: {
      oldpwd,
      newpwd,
    },
  });
}

export function fetchUpdateUserRole({ id, user_roles }: IUser) {
  return request({
    url: `/user/update_user_role/${id}`,
    method: 'put',
    data: {
      user_roles,
    },
  });
}
