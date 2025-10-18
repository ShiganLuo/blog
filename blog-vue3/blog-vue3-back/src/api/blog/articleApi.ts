import request from '@/utils/http'
import { ArticleResult, ArticleRecordResult } from '@/types/blog/article'
import { CodeMsgResult } from '@/types/axios'

// 文章
export class ArticleService {
  // 查询文章列表
  static listArticle(query: any) {
    return request.post<ArticleRecordResult>({
      url: '/api/admin/articles/getArticleList',
      data: query
    })
  }

  // 查询文章详细
  static getArticle(id: any) {
    return request.get<ArticleResult>({
      url: '/api/admin/articles/getArticleById/' + id
    })
  }

  // 新增文章 修改文章
  static addOrUpdateArticle(data: any) {
    return request.post({
      url: '/blog/article/admin',
      data: data
    })
  }

  // 逻辑删除或恢复文章
  static updateArticle(data: any) {
    return request.put({
      url: '/blog/article/admin',
      data: data
    })
  }

  // 删除文章
  static deleteArticle(ids: Array<number>) {
    return request.del({
      url: '/blog/article/admin/delete',
      headers: {
        'Content-Type': 'application/json'
      },
      data: ids
    })
  }

  // 置顶或推荐文章
  static updateTopOrFeatured(data: any) {
    return request.put({
      url: '/blog/article/admin/topAndFeatured',
      headers: {
        'Content-Type': 'application/json'
      },
      data: data
    }) 
  }

  // 导出文章列表
  static exportExcel(data: any) {
    return request.post({
      url: 'blog/article/export',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      },
      responseType: 'blob',
      data: data
    })
  }
}
