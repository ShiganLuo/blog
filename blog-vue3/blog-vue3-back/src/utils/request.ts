import axios, { AxiosRequestConfig, AxiosResponse } from 'axios';

import router from '@/router';
import { useUserStore } from '@/store/user';

import { getCurrEnv } from '@/utils/localStorage/env'
import { _getLocalItem } from '@/utils/localStorage/cache';

const config: AxiosRequestConfig = {
  // baseURL: '/api/', // 本地开发：/api/，线上正式服：/prodapi/，线上测试服：/betaapi/
  timeout: 1000 * 5,
  withCredentials: true, // 允许跨域携带cookie信息
  headers: {
    Accept: 'application/json, text/plain, */*',
    'Content-Type': 'application/json',
    'X-Requested-With': 'XMLHttpRequest'
  }
};
const service = axios.create(config);

export interface IResponse<T> {
  code: number;
  result: T;
  message: string;
}

// 请求拦截
service.interceptors.request.use(
  (cfg) => {
    console.log(getCurrEnv())
    switch (getCurrEnv()) {
      case 'prod':
        cfg.baseURL = '';
        break;
      case 'beta':
        cfg.baseURL = '';
        break;
      case 'development':
        cfg.baseURL = '';
        break;
    }
    const token = _getLocalItem('accessToken');
    if (token) {
      // @ts-ignore
      cfg.headers.Authorization = `Bearer ${token}`;
    }
    return cfg;
  },
  (error) => {
    console.log(error);
    return Promise.reject(error);
  }
);

// 响应拦截
service.interceptors.response.use(
  // 2xx
  (response:AxiosResponse<IResponse<any>>) => {
    return response.data;
  },
  // not 2xx
  (error) => {
    console.log('响应拦截到错误', error);
    if (error.message.indexOf('timeout') !== -1) {
      window.$message.error(error.message);
      return;
    }
    const userStore = useUserStore();
    const statusCode = error.response.status as number;
    const errorResponseData = error.response.data;
    switch (statusCode) {
      case 400:
        window.$message.error(errorResponseData.message || '请求参数错误');
        return Promise.reject(errorResponseData);
      case 401:
        window.$message.error(errorResponseData.message || '登录状态失效');
        userStore.logout();
        router.push('/login');
        // 特殊处理，例如通知父窗口登录过期
        if (window.opener) {
          window.opener.postMessage({ type: 'login_expired' }, '*');
        }
        // 对于 401，返回 reject 以便外部捕获，同时执行跳转
        return Promise.reject(errorResponseData);
      case 403:
        window.$message.error(errorResponseData.message || '没有权限访问');
        return Promise.reject(errorResponseData);
      case 404:
        window.$message.error(errorResponseData.message || '资源不存在');
        return Promise.reject(errorResponseData);
      case 500:
        // 针对500错误，如果服务器有自定义错误信息，优先使用
        const msg = errorResponseData.message || errorResponseData.error || '服务器内部错误';
        window.$message.error(msg);
        return Promise.reject(msg);
      default:
        // 处理其他不在白名单内的状态码
        window.$message.error(error.message || '未知错误');
        return Promise.reject(error.message);
    }
  }
);
// export default service;

export function request<T = any>(config: AxiosRequestConfig): Promise<IResponse<T>> {
  return service.request<any, IResponse<T>>(config);
}