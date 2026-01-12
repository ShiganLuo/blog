import { BaseArrayResult, BaseObjectResult, BasePageResult, BaseRecordResult } from '../axios'

export interface CommentResult {
  id: string
  userId: string
  userName: string
  forId: string
  commentContent: string
  replyUserId: string
  replyUserName: string
  parentId: string
  type: string
  isDeleted: string
  status: string
  createdAt: string
  updatedAt: string
}

export type CommentListPageResult = BasePageResult<CommentResult>
export type CommentListResult = BaseArrayResult<CommentResult>
export type CommentInfoResult = BaseObjectResult<CommentResult>
export type CommentRecordResult = {
  list:CommentResult[]
  total:number
}
