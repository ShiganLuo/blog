import { BaseArrayResult, BaseObjectResult, BasePageResult } from '../axios'

export interface WebsiteResult {
    websiteChineseName: string
    websiteEnglishName: string
    websiteTitle: string
    websiteIntro: string
    frontHeadBackground: string
    logo: string
    notice: string
    icpFilingNumber: string
    psbFilingNumber: string
    author: string
    authorAvatar: string
    authorIntro: string
    authorPersonalSay: string
    userAvatar: string
    touristAvatar: string
    multiLanguage: number
    websiteCreateTime: string
    github: string
    gitee: string
    bilibili: string
    qq: string
    qqGroup: string
    wechat: string
    wechatGroup: string
    weibo: string
    csdn: string
    zhihu: string
    juejin: string
    twitter: string
    stackoverflow: string
    isCommentReview: number
    isEmailNotice: number
    weiXinQRCode: string
    alipayQRCode: string
    favicon: string
}

export type WebsiteListPageResult = BasePageResult<WebsiteResult>
export type WebsiteListResult = BaseArrayResult<WebsiteResult>
export type WebsiteRecordResult = BaseObjectResult<WebsiteResult>
