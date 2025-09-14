import axios from 'axios'
import type {
  AxiosInstance,
  AxiosRequestConfig,
  AxiosResponse,
  InternalAxiosRequestConfig
} from 'axios'
import type { BaseResponse } from './types'
import { API_CODE } from './types'

const service: AxiosInstance = axios.create({
  timeout: 10000,
  headers: {
    Accept: 'application/json, text/plain, */*',
    'Content-Type': 'application/json',
    'X-Requested-With': 'XMLHttpRequest'
  }
})

// 请求拦截器
service.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  config.headers = config.headers || {}
  config.headers['Authorization'] = localStorage.getItem('token') || ''
  return config
})

// 响应拦截器（泛型支持关键点 ✅）
service.interceptors.response.use(
  <T>(response: AxiosResponse<BaseResponse<T>>): T => {
    const res = response.data

    switch (res.code) {
      case API_CODE.SUCCESS:
        return res.result as T // ✅ 强制返回泛型 result 字段
      case API_CODE.UNAUTHORIZED:
        window.location.href = '/login'
        throw new Error('请重新登录')
      default:
        throw new Error(res.message || '请求失败')
    }
  },
  (error) => {
    const messageMap: Record<number, string> = {
      400: '请求错误',
      401: '未授权',
      403: '拒绝访问',
      404: '资源不存在',
      500: '服务器错误'
    }
    const message = messageMap[error.response?.status] || '网络连接异常'
    return Promise.reject(new Error(message))
  }
)

/**
 * 简化请求（返回 result 字段内容）
 * @param config 请求配置
 */
export function request<T = any>(config: AxiosRequestConfig): Promise<T> {
  return service.request<any, T>(config) // ✅ 关键泛型位置
}

/**
 * 获取完整结构（code + message + result）
 * @param config 请求配置
 */
export function fullRequest<T = any>(
  config: AxiosRequestConfig
): Promise<BaseResponse<T>> {
  return axios.request<BaseResponse<T>>(config).then((res) => res.data)
}

export default service
