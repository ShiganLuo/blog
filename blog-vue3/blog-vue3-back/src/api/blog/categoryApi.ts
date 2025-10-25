import request from '@/utils/http'
import { CategoryRecordResult, CategoryListResult } from '@/types/blog/category'
import { CodeMsgResult } from '@/types/axios'

// 分类
export class CategoryService {
  // 查询分类列表
  static listCategory(query: any) {
    return request.get<CategoryRecordResult>({
      url: '/blog/category/admin/categories',
      params: query
    })
  }

  // 新增分类
  static addCategory(data: any) {
    return request.post({
      url: '/blog/category/admin/categories',
      data: data
    })
  }

  // 修改分类
  static updateCategory(data: any) {
    return request.post({
      url: '/blog/category/admin/categories',
      data: data
    })
  }

  // 删除分类
  static deleteCategory(id: any) {
    return request.del({
      url: '/blog/category/admin/categories',
      headers: {
        'Content-Type': 'application/json'
      },
      data: id
    })
  }

  // 搜索分类
  static searchCategories() {
    return request.get<CategoryListResult>({
      url: '/api/admin/categories/getCategoryDictionary',
    })
  }
}
