import { BaseArrayResult, BaseObjectResult, BasePageResult } from '../axios'

export interface ArticleResult {
  id: string
  authorName: string
  articleCover: string
  articleTitle: string
  categoryNameList: string[]
  articleAbstract: string
  articleContent: string
  isTop: string
  isFeatured: string
  isDeleted: string
  status: string
  type: string
  originalUrl: string
  createAt: string
  updateAt: string
  tagNameList: string[]
}

export type ArticleListPageResult = BasePageResult<ArticleResult>
export type ArticleListResult = BaseArrayResult<ArticleResult>
export type ArticleRecordResult = {
  list:ArticleResult[]
  total:number
}
