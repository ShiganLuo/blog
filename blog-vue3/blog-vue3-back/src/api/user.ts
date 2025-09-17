import { IUser } from '@/interface';
import request, { IResponse } from '@/utils/request';

export interface UserLoginResponse {
  accessToken: string;
  refreshToken: string;
  expires: string;        // 也可以用 Date，如果在代码中转成 Date 对象
  id: number;
  avatar: string | null;
  username: string;
  nickname: string | null;
  roles: string;          // 如果有可能是数组，也可以改成 string[]
}

export function fetchLogin({ username, password }) {
  return request<IResponse<UserLoginResponse>>({
    url: '/api/admin/users/login',
    method: 'post',
    data: { username, password },
  });
}

export function fetchUserInfo() {
  return request({
    url: '/user/get_user_info',
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
  return request.get(`/user/find/${id}`);
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
