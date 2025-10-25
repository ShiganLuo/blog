import { BaseArrayResult, BaseObjectResult, BasePageResult } from '../axios'

export interface ArticleResult {
  id: number
  authorName: string
  articleCover: string
  articleTitle: string
  categoryNameList: string[]
  articleAbstract: string
  articleContent: string
  isTop: number
  isFeatured: number
  isDeleted: number
  status: string
  type: string
  originalUrl: string
  viewsCount: number
  createdAt: string
  updatedAt: string
  tagNameList: string[]
}

export type ArticleListPageResult = BasePageResult<ArticleResult>
export type ArticleListResult = BaseArrayResult<ArticleResult>
export type ArticleRecordResult = {
  list:ArticleResult[]
  total:number
}
