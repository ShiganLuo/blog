import { defineStore } from 'pinia';

import { fetchEmailCodeLogin, fetchRegister } from '@/api/emailUser';
import { fetchLogin, fetchUserInfo } from '@/api/user';
import { IRole,UserLoginResponse } from '@/interface';
import { asyncRoutes } from '@/router/index';
import cache, {_getLocalItem,_setLocalItem,_removeLocalItem} from '@/utils/localStorage/cache';

type UserInfo = {
  id:string;
  avatar:string;
  username:string;
  nickname:string;
  roles:string;
};

export const useUserStore = defineStore('user', {
  state: () => {
    return {
      accessToken: '',
      refreshToken: '',
      expires: '',
      id: '',
      avatar: '',
      username: '',
      nickname: '',
      roles: '',
    } as UserLoginResponse;
  },
  actions: {
    setUserInfo(userInfo:UserInfo) {
      _setLocalItem('userInfo',userInfo)
      this.id = userInfo.id;
      this.avatar = userInfo.avatar;
      this.username = userInfo.username;
      this.nickname = userInfo.nickname;
    },
    setExpires(expires:string) {
      _setLocalItem('expires',expires);
      this.expires = expires;
    },
    setAcessToken(accessToken:string) {
      _setLocalItem('accessToken',accessToken)
      this.accessToken = accessToken;
    },
    setRefreshToken(refreshToken:string) {
      _setLocalItem("refreshToken",refreshToken);
      this.refreshToken = refreshToken;
    },
    setRoles(res) {
      this.roles = res;
    },
    logout() {
      _removeLocalItem('accessToken');
      _removeLocalItem('refreshToken');
      _removeLocalItem('expires')
      this.accessToken = '';
      this.refreshToken = '';
      this.roles = '';
    },
    async pwdLogin({ username, password }) {
      try {
        const res = await fetchLogin({
          username,
          password,
        });
        const {accessToken, refreshToken, expires,...userInfo} = res.result
        this.setAcessToken(accessToken);
        this.setRefreshToken(refreshToken);
        this.setExpires(expires);
        this.setUserInfo(userInfo);
        return true;
      } catch (error: any) {
        // 错误返回401，全局的响应拦截会打印报错信息
        return false;
      }
    },
    async codeLogin({ email, code }) {
      try {
        const res = await fetchEmailCodeLogin({
          email,
          code,
        });
        const {accessToken, refreshToken,expires,...userInfo} = res.result
        this.setAcessToken(accessToken);
        this.setRefreshToken(refreshToken);
        this.setExpires(expires);
        this.setUserInfo(userInfo);
        return true;
      } catch (error: any) {
        // 错误返回401，全局的响应拦截会打印报错信息
        return false;
      }
    },
    async register({ email, code }) {
      try {
        // @ts-ignore
        const { data: token } = await fetchRegister({
          email,
          code,
        });
        return { token };
      } catch (error: any) {
        window.$message.error(error.message);
        return error;
      }
    },
    getUserInfo() {
      const userInfo: UserInfo = _getLocalItem("userInfo");
      return userInfo;
    },
    generateAsyncRoutes(roles) {
      // 比较两数组是否有交集(返回true代表有交集)
      const hasMixin = (a, b) => {
        return a.length + b.length !== new Set([...a, ...b]).size;
      };
      const myRole = roles.map((v) => v.role_value);
      const handleAsyncRoutes = (roleRoutes) => {
        const deepFind = (route) => {
          const res: any[] = [];
          route.forEach((v) => {
            const t = { ...v };
            if (t.meta && t.meta.roles) {
              // 有meta数据，且meta有roles数据，开始判断权限，有权限才允许访问
              const hasRole = hasMixin(t.meta.roles, myRole);
              hasRole && res.push(t);
            } else {
              // 没有meta信息，允许访问
              res.push(t);
            }
            if (t.children) {
              t.children = deepFind(t.children);
            }
          });
          return res;
        };
        const res = deepFind(roleRoutes);
        return res;
      };
      return handleAsyncRoutes(asyncRoutes);
    },
  },
});
