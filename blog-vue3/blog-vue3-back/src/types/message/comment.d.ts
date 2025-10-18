import { BaseArrayResult, BaseObjectResult, BasePageResult, BaseRecordResult } from '../axios'

export interface CommentResult {
  id: string
  userId: string
  topicId: string
  commentContent: string
  replyUserId: string
  parentId: string
  type: string
  isDelete: string
  isReview: string
  createTime: string
  updateTime: string
}

export type CommentListPageResult = BasePageResult<CommentResult>
export type CommentListResult = BaseArrayResult<CommentResult>
export type CommentInfoResult = BaseObjectResult<CommentResult>
export type CommentRecordResult = {
  list:CommentResult[]
  total:number
}
