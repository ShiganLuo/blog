import { BaseArrayResult, BaseObjectResult, BasePageResult } from '../axios'

// 后台说说
export interface TalkResult {
  id: number
  content: string
  images: string
  isTop: number
  status: number
  createTime: string
  imgs?: string[]
}

export type TalkListPageResult = BasePageResult<TalkResult>
export type TalkListResult = BaseArrayResult<TalkResult>
export type TalkRecordResult = {
  list:TalkResult[]
  total:number
}
