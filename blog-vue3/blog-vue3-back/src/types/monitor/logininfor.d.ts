import { BaseArrayResult, BaseObjectResult, BasePageResult } from '../axios'

export interface LogininforResult {
  infoId: string
  userName: string
  ipaddr: string
  loginLocation: string
  browser: string
  os: string
  status: string
  msg: string
  loginTime: string
}

export type LogininforListPageResult = {
  list:LogininforResult[]
  total:number
}
export type LogininforListResult = BaseArrayResult<LogininforResult>
export type LogininforInfoResult = BaseObjectResult<LogininforResult>
