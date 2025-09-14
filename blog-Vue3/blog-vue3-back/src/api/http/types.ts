// 基础响应类型
export interface BaseResponse<T = any> {
    code: number
    message: string
    result: T
}
    
// 业务错误码枚举
export enum API_CODE {
    SUCCESS = 200,
    BAD_REQUEST = 400,
    UNAUTHORIZED = 401,
    FORBIDDEN = 403,
    NOT_FOUND = 404,
    SERVER_ERROR = 500
}