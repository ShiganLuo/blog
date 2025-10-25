import { BaseArrayResult, BaseObjectResult, BasePageResult, BaseRecordResult } from '../axios'

export interface CategoryResult {
  id: string
  categoryName: string
  createdAt: string
  updatedAt: string
}

export type CategoryListPageResult = BasePageResult<CategoryResult>
export type CategoryListResult = CategoryResult[]
export type CategoryInfoResult = BaseObjectResult<CategoryResult>
export type CategoryRecordResult = {
  list:CategoryResult[]
  total:number
}
