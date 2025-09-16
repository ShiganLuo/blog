import Cookies from "js-cookie";
import { storageSession } from "@pureadmin/utils";
import { useUserStoreHook } from "@/store/modules/user";

export interface DataInfo<T> {
    accessToken: string; // token
    refreshToken: string;
    expires: string;
    avatar: string;
    username: string; // 用户名
    nickname: string; //
    role: number; // 用户角色
    id: number; // 用户id
}

export const sessionKey = "user-info";
export const TokenKey = "authorized-token";

/** 获取`token` */
export function getToken(): DataInfo<number> {
  // 此处与`TokenKey`相同，此写法解决初始化时`Cookies`中不存在`TokenKey`报错
  return Cookies.get(TokenKey)
    ? JSON.parse(Cookies.get(TokenKey))
    : storageSession().getItem(sessionKey);
}

/**
 * 设置token
 */
export function setToken(data: DataInfo<string>) {
  const { accessToken } = data;

  function setSessionKey(username: string, role: number) {
    useUserStoreHook().SET_USERNAME(username);
    useUserStoreHook().SET_ROLE(role);
    storageSession().setItem(
      sessionKey,
      JSON.stringify({
        username,
        role
      })
    );
  }

  Cookies.set(TokenKey, JSON.stringify({ accessToken }));

  if (data.username && data.role) {
    const { username, role } = data;
    setSessionKey(username, role);
  } else {
    const username =
      storageSession().getItem<DataInfo<number>>(sessionKey)?.username ?? "";
    const role = storageSession().getItem<DataInfo<number>>(sessionKey)?.role;
    setSessionKey(username, role);
  }
}

/** 删除`token`以及key值为`user-info`的session信息 */
export function removeToken() {
  Cookies.remove(TokenKey);
  sessionStorage.clear();
}
