import { BaseArrayResult, BaseObjectResult, BasePageResult } from '../axios'

export interface JobResult {
  jobId: string
  jobName: string
  jobGroup: string
  invokeTarget: string
  cronExpression: string
  misfirePolicy: string
  concurrent: string
  status: string
  createBy: string
  createTime: string
  updateBy: string
  updateTime: string
  remark: string
}

export type JobListPageResult = {
  list:JobResult[]
  total:number
}
export type JobListResult = BaseArrayResult<JobResult>

