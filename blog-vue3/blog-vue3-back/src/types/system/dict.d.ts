import { DictType } from '@/utils/dict'
import { BaseArrayResult, BaseObjectResult, BasePageResult } from '../axios'

export interface DictDataResult {
  dictCode: string
  dictSort: string
  dictLabel: string
  dictValue: string
  dictType: string
  cssClass: string
  listClass: string
  isDefault: string
  status: string
  createBy: string
  createTime: string
  updateBy: string
  updateTime: string
  remark: string
}

// export type DictDataListPageResult = BasePageResult<DictDataResult>
export type DictDataListPageResult = {
  list: DictDataResult[]
  total: number
}
export type DictDataListResult = BaseArrayResult<DictDataResult>


import { BaseArrayResult, BaseObjectResult, BasePageResult } from '../axios'

export interface DictTypeResult {
  dictId: string
  dictName: string
  dictType: string
  status: string
  createBy: string
  createTime: string
  updateBy: string
  updateTime: string
  remark: string
}

// export type DictTypeListPageResult = BasePageResult<DictTypeResult>
export interface DictTypeListPageResult {
  list: DictTypeResult[]
  total: number
}
// export type DictTypeListResult = BaseArrayResult<DictTypeResult>
export type DictTypeListResult = DictTypeResult[]

