import request from '@/utils/http'
import { ArticleResult, ArticleRecordResult } from '@/types/blog/article'

export class ArticleService {
  static listArticle(query: any) {
    return request.post<ArticleRecordResult>({
      url: '/admin/articles/getArticleList',
      data: query
    })
  }

  static getArticleById(id: any) {
    return request.get<ArticleResult>({
      url: '/admin/articles/getArticleById/' + id
    })
  }

  static addOrUpdateArticle(data: any) {
    return request.post({
      url: '/admin/articles/createOrupdateArticles',
      data: data
    })
  }

  static deleteArticle(ids: Array<number>) {
    return request.post({
      url: '/admin/articles/updateArticlesDeletedStatus',
      headers: { 'Content-Type': 'application/json' },
      data: { ids: ids, isDeleted: true }
    })
  }

  static updateTopOrFeatured(data: any) {
    return request.post({
      url: '/admin/articles/updateArticleRecommendStatus',
      headers: { 'Content-Type': 'application/json' },
      data: data
    })
  }

  static exportExcel(data: any) {
    return request.post({
      url: '/admin/articles/getArticleList',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      responseType: 'blob',
      data: data
    })
  }

  static uploadCover(data: any) {
    return request.post({
      url: '/admin/articles/uploadCover',
      data: data
    })
  }
}
