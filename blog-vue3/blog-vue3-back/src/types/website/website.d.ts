import { BaseArrayResult, BaseObjectResult, BasePageResult } from '../axios'

export interface WebsiteResult {
    websiteChineseName: string
    websiteEnglishName: string
    websiteTitle: string
    websiteIntro: string
    logo: string
    notice: string
    icpFilingNumber: string
    psbFilingNumber: string
    author: string
    authorAvatar: string
    authorIntro: string
    userAvatar: string
    multiLanguage: number
    websiteCreateTime: string
    github: string
    gitee: string
    qq: string
    wechat: string
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
