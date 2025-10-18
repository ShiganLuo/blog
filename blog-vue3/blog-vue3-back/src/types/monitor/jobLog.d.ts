import { BaseArrayResult, BaseObjectResult, BasePageResult } from '../axios'

export interface JobLogResult {
  jobLogId: string
  jobName: string
  jobGroup: string
  invokeTarget: string
  jobMessage: string
  status: string
  exceptionInfo: string
  createTime: string
}

export type JobLogListPageResult = {
  list:JobLogResult[]
  total:number
}
export type JobLogListResult = BaseArrayResult<JobLogResult>
export type JobLogInfoResult = BaseObjectResult<JobLogResult>
