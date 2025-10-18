import { BaseArrayResult, BaseObjectResult, BasePageResult } from '../axios'

export interface FriendLinkResult {
  id: string
  linkName: string
  linkAvatar: string
  linkAddress: string
  linkIntro: string
  createTime: string
  updateTime: string
}

export type FriendLinkListPageResult = BasePageResult<FriendLinkResult>
export type FriendLinkListResult = BaseArrayResult<FriendLinkResult>
export type FriendLinkInfoResult = BaseObjectResult<FriendLinkResult>
export type FriendLinkRecordResult = {
  list: FriendLinkResult[]
  total:number
}



