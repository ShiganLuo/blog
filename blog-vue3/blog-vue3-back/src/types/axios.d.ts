export interface UserLoginResponse {
  accessToken: string;
  refreshToken: string;
  expires: string;        // 也可以用 Date，如果在代码中转成 Date 对象
  id: string;
  avatar: string;
  username: string;
  nickName: string;
  roles: string[];          // 如果有可能是数组，也可以改成 string[]
}

export type ErrorMessageMode = 'none' | 'modal' | 'message' | undefined

export interface RequestOptions {
  joinParamsToUrl: boolean
  formatDate: boolean
  isTransformResponse: boolean
  isReturnNativeResponse: boolean
  joinPrefix: boolean
  apiUrl: string
  errorMessageMode: ErrorMessageMode
  joinTime: boolean
  ignoreCancelToken: boolean
  withToken: boolean
}

// 基础接口返回的数据结构
export interface BaseResult<T = any> {
  data: T // 数据
  token: string // 可选字段，用于返回 token
}

// 分页返回数据结构
export interface BasePageResult<T> {
  rows: T[]
  total: number
  code: number // 状态码
  msg: string // 消息
}

// 分页数据结构，继承基础结果结构
export interface PaginationResult<T> extends BaseResult {
  currentPage: number // 当前页
  pageSize: number // 每页条数
  lastPage: number // 总页数
  total: number // 总条数
  data: T
}

// 基础数组返回数据结构
export interface BaseArrayResult<T = any> {
  data: T[] // 数据
}

// 基础对象返回数据结构
export interface BaseObjectResult<T = any> {
  code: number // 状态码
  msg: string // 消息
  data: T // 数据
}

// code msg 返回数据结构
export interface CodeMsgResult {
  code: number
  msg: string
}

export interface BaseRecordResult<T = any> {
  code: number // 状态码
  msg: string // 消息
  data: {
    records: T[]
    count: number
  }
}
