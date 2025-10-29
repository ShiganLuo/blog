import request from '@/utils/http'
import { DictDataResult, DictDataListPageResult } from '@/types/system/dict'
import { CodeMsgResult } from '@/types/axios'

// 字典数据
export class DictDataService {
  // 查询字典数据列表
  static listData(type: any) {
    return request.get<DictDataListPageResult>({
      url: `/api/admin/settings/getDictSetting/${type}`,
    })
  }

  // 查询字典数据详细
  static getData(dictCode: any) {
    return request.get<DictDataResult>({
      url: '/system/dict/data/' + dictCode
    })
  }

  // 新增字典数据
  static addData(data: any) {
    return request.post<CodeMsgResult>({
      url: '/system/dict/data',
      data: data
    })
  }

  // 修改字典数据
  static updateData(data: any) {
    return request.put<CodeMsgResult>({
      url: '/system/dict/data',
      data: data
    })
  }

  // 删除字典数据
  static deleteData(dictCode: any) {
    return request.del<CodeMsgResult>({
      url: '/system/dict/data/' + dictCode
    })
  }

  // 导出字典数据列表
  static exportExcel(data: any) {
    return request.post({
      url: 'system/dict/data/export',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      },
      responseType: 'blob',
      data: data
    })
  }
}
