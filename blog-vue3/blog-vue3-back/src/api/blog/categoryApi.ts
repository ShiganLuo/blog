import request from '@/utils/http'
import { CategoryRecordResult, CategoryListResult } from '@/types/blog/category'

export class CategoryService {
  static listCategory(query: any) {
    return request.post<CategoryRecordResult>({
      url: '/admin/categories/list',
      data: query
    })
  }

  static addCategory(data: any) {
    return request.post({
      url: '/admin/categories/create',
      data: data
    })
  }

  static updateCategory(data: any) {
    return request.post({
      url: '/admin/categories/uploadCategory',
      data: data
    })
  }

  static deleteCategory(id: any) {
    return request.del({
      url: '/admin/categories/' + id,
      headers: { 'Content-Type': 'application/json' }
    })
  }

  static searchCategories(keywords: string) {
    return request.get<CategoryListResult>({
      url: '/admin/categories/getCategoryDictionary',
      params: { keyword: keywords }
    })
  }
}
