import { BaseArrayResult, BaseObjectResult, BasePageResult } from '../axios'

export interface FriendLinkResult {
  id: string
  siteName: string
  siteLogo: string
  siteUrl: string
  siteDesc: string
  userName: string
  status: number
  statusName: string
  sortOrder: number
  isVisible: number
  applyMessage: string
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



