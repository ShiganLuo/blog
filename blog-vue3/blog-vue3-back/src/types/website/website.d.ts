import { BaseArrayResult, BaseObjectResult, BasePageResult } from '../axios'

export interface WebsiteResult {
    name: string
    englishName: string
    author: string
    authorAvatar: string
    authorIntro: string
    logo: string
    multiLanguage: number
    notice: string
    websiteCreateTime: string
    beianNumber: string
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
    userAvatar: string
    isCommentReview: number
    isEmailNotice: number
    isReward: number
    weiXinQRCode: string
    alipayQRCode: string
    favicon: string
    websiteTitle: string
    gonganBeianNumber: string
}

export type WebsiteListPageResult = BasePageResult<WebsiteResult>
export type WebsiteListResult = BaseArrayResult<WebsiteResult>
export type WebsiteRecordResult = BaseRecordResult<WebsiteResult>
