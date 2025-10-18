import { BaseArrayResult, BaseObjectResult, BasePageResult } from '../axios'

export interface ArticleResult {
  id: string
  userId: string
  categoryId: string
  articleCover: string
  articleTitle: string
  articleAbstract: string
  articleContent: string
  isTop: string
  isFeatured: string
  isDelete: string
  status: string
  type: string
  password: string
  originalUrl: string
  createTime: string
  updateTime: string
}

export type ArticleListPageResult = BasePageResult<ArticleResult>
export type ArticleListResult = BaseArrayResult<ArticleResult>
export type ArticleRecordResult = {
  list:ArticleResult[]
  total:number
}
