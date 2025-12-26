import { BaseArrayResult, BaseObjectResult, BasePageResult } from '../axios'

export interface WebsiteResult {
    websiteTitle: string
    name: string
    logo: string
    notice: string
    ICPFilingNumber: string
    PSBFilingNumber: string
    englishName: string
    author: string
    authorAvatar: string
    authorIntro: string
    userAvatar: string
    multiLanguage: number
    websiteCreateTime: string
    qqLogin: number
    github: string
    gitee: string
    qq: string
    weChat: string
    weibo: string
    csdn: string
    zhihu: string
    juejin: string
    twitter: string
    stackoverflow: string
    touristAvatar: string
    isCommentReview: number
    isEmailNotice: number
    isReward: number
    weiXinQRCode: string
    alipayQRCode: string
    favicon: string
}

export type WebsiteListPageResult = BasePageResult<WebsiteResult>
export type WebsiteListResult = BaseArrayResult<WebsiteResult>
export type WebsiteRecordResult = BaseRecordResult<WebsiteResult>
