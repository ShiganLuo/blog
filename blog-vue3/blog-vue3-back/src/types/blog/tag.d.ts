import { BaseArrayResult, BaseObjectResult, BasePageResult, BaseRecordResult } from '../axios'

export interface TagResult {
  id: string
  tagName: string
  createdAt: string
  updatedAt: string
}

export type TagListPageResult = BasePageResult<TagResult>
export type TagListResult = TagResult[]
export type TagInfoResult = BaseObjectResult<TagResult>
export type TagRecordResult = {
  list:TagResult[]
  total:number
}
