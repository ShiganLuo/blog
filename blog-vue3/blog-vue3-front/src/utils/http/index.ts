import axios from 'axios'
import type {
  AxiosInstance,
  AxiosRequestConfig,
  AxiosResponse,
  InternalAxiosRequestConfig
} from 'axios'
import type { IResponse } from './types'
import { ApiStatus,CodeMessage } from './types'
import { useUserStore } from '@/stores/index'
import { ElMessage,ElMessageBox } from 'element-plus'
import EmojiText from '../emojo'
import { tansParams } from '@/utils/tool'
const axiosInstance : AxiosInstance = axios.create({
  timeout: 10000,
  baseURL: import.meta.env.VITE_API_BASE_URL,
  headers: {
    Accept: 'application/json, text/plain, */*',
    'Content-Type': 'application/json',
    'X-Requested-With': 'XMLHttpRequest'
  }
})

// 请求拦截器
axiosInstance.interceptors.request.use(
  (request: InternalAxiosRequestConfig) => {
    const { accessToken } = useUserStore()
    if (accessToken) {
      request.headers.set({
        Authorization: `Bearer ${accessToken}`
      })
    }

    return request
  },
  (error) => {
    ElMessage.error(`服务器异常！ ${EmojiText[ApiStatus.SERVER_ERROR]}`)
    return Promise.reject(error)
  }
)

// 响应拦截器
let isRefreshing = false
let requests: ((token: string | null) => void)[] = []

axiosInstance.interceptors.response.use(
  async (response: AxiosResponse) => {
    const code = response.data.code || ApiStatus.SUCCESS // 业务状态码
    const msg = response.data.message || CodeMessage['default']

    // 二进制数据直接返回
    if (response.config.responseType === 'blob' || response.config.responseType === 'arraybuffer') { // response.request.responseType类型不安全
      return response
    }

    // 401 -> token过期，尝试刷新
    if (code == ApiStatus.SUCCESS) {
      return Promise.resolve(response)
    } else if (code === ApiStatus.UNAUTHORIZED) {
      const userStore = useUserStore()
      const originalRequest = response.config
      // 刷新请求报 401，但是isRefreshing为True，踢用户下线
      if (originalRequest.url?.includes('/admin/users/refreshToken')) {
        isRefreshing = false // 强制解锁
        requests.forEach(cb => cb(null))
        requests = []
        userStore.logOut()
        return Promise.reject(new Error('Refresh Token 失效'))
      }
      if (!isRefreshing) {
        isRefreshing = true
        try {
          // 调用刷新 token 接口
          const refreshRes = await axiosInstance.post('/admin/users/refreshToken', {
            refreshToken: userStore.refreshToken
          })
          const newAccessToken = refreshRes.data.result
          userStore.setToken(newAccessToken)

          // 让所有挂起请求重新执行
          requests.forEach((cb) => cb(newAccessToken))
          requests = []

          // 重试原请求
          originalRequest.headers.set('Authorization', `Bearer ${newAccessToken}`)
          return axiosInstance(originalRequest)
        } catch (err) {
          // 刷新失败，退出登录
          userStore.logOut()
          ElMessageBox.alert('登录状态已过期，请重新登录', '系统提示', { type: 'warning' })
          return Promise.reject(err)
        } finally {
          isRefreshing = false
        }
      } else {
        // 已经在刷新 token，把请求挂起
        return new Promise((resolve,reject) => {
          requests.push((newAccessToken: string | null) => {

            if (!newAccessToken) {
              reject(new Error('刷新 token 失败'))
              return
            }
            originalRequest.headers.set('Authorization', `Bearer ${newAccessToken}`)
            resolve(axiosInstance(originalRequest))
          })
        })
      }
    } 
    // 其他业务状态处理
    else if (code === ApiStatus.SERVER_ERROR) {
      ElMessage({ message: msg, type: 'error' })
      return Promise.reject(new Error(msg))
    } else {
      ElMessage({ message: msg, type: 'warning' })
      return Promise.reject(new Error(msg)) 
    }
  },
  (error) => {
    if (axios.isCancel(error)) {
      console.log('repeated request: ' + error.message)
    } else {
      const errorMessage = error.response?.data?.message
      ElMessage.error(
        errorMessage
          ? `${errorMessage} ${EmojiText[ApiStatus.SERVER_ERROR]}`
          : `请求超时或服务器异常！${EmojiText[ApiStatus.SERVER_ERROR]}`
      )
    }
    return Promise.reject(error)
  }
)

// 请求
async function request<T = any>(config: AxiosRequestConfig): Promise<T> {
  // get请求映射params参数
  if (config.method === 'GET' && config.params) {
    let url = config.url + '?' + tansParams(config.params)
    url = url.slice(0, -1)
    config.params = {}
    config.url = url
  }
  try {
    const res = await axiosInstance.request<T>({ ...config })
    return res.data
  } catch (e) {
    if (axios.isAxiosError(e)) {
      // 可以在这里处理 Axios 错误
    }
    return Promise.reject(e)
  }
}

// API 方法集合
const api = {
  get<T>(config: AxiosRequestConfig): Promise<IResponse<T>> {
    return request({ ...config, method: 'GET' }) // GET 请求
  },
  post<T>(config: AxiosRequestConfig): Promise<IResponse<T>> {
    return request({ ...config, method: 'POST' }) // POST 请求
  },
  put<T>(config: AxiosRequestConfig): Promise<IResponse<T>> {
    return request({ ...config, method: 'PUT' }) // PUT 请求
  },
  del<T>(config: AxiosRequestConfig): Promise<IResponse<T>> {
    return request({ ...config, method: 'DELETE' }) // DELETE 请求
  }
}

export default api
