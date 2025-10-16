// 基础响应类型
export interface IResponse<T = any> {
    code: number
    message: string
    result: T
}
    
// 业务错误码枚举
export enum ApiStatus {
    SUCCESS = 200,
    BAD_REQUEST = 400,
    UNAUTHORIZED = 401,
    FORBIDDEN = 403,
    NOT_FOUND = 404,
    SERVER_ERROR = 500
}

export const CodeMessage = {
  '401': '认证失败，无法访问系统资源',
  '403': '当前操作没有权限',
  '404': '访问资源不存在',
  default: '系统未知错误，请反馈给管理员'
}
