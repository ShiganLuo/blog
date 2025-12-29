import { BaseArrayResult, BaseObjectResult, BasePageResult } from '../axios'

export interface FriendLinkResult {
  id: string
  siteName: string
  siteAvatar: string
  siteUrl: string
  siteDesc: string
  createdAt: string
  updatedAt: string
}

export type FriendLinkListPageResult = BasePageResult<FriendLinkResult>
export type FriendLinkListResult = BaseArrayResult<FriendLinkResult>
export type FriendLinkInfoResult = BaseObjectResult<FriendLinkResult>
export type FriendLinkRecordResult = {
  list: FriendLinkResult[]
  total:number
}



