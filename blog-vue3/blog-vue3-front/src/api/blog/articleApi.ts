import request from "@/utils/http/index";
import { type ArticleListResponse,
        type ArticleInfo, 
        type recommendArticles,
        type ArticleInfoListResponse,
        type homeGetArticleListParam,
        type ArticleSearch
} from "@/types/blog/article";


export class ArticleService {

  static homeGetArticleList(data?: homeGetArticleListParam) {
    return request.post<ArticleInfoListResponse>({
        url: '/api/front/articles/getArticleList',
        data
    })
  }

  static getHotArticle(data?: object) {
    return request.post<ArticleInfo[]>({
      url: '',
      data
    })
  }

  static getArticleByContent(data?: object) {
    return request.post<ArticleSearch[]>({
      url: '',
      data
    })
  }

  static getArticleById(id?: string | number) {
    if (id == null) {
      throw new Error('getArticleById: id is required')
    }
    return request.get<ArticleInfo>({
      url: `/api/front/articles/getArticleById/${id}`,
    })
  }

  static getArticleListByTagId(data?: object) {
    return request.post<ArticleListResponse>({
      url: '/api/front/articles/getArticlesByTagId',
      data
    })
  }

  static getArticleListByCategoryId(data?: object) {
    return request.post<ArticleListResponse>({
      url: '/api/front/articles/getArticlesByCategoryId',
      data
    })
  }

  static getRecommendArticleById(id?: string | number) {
    return request.get<recommendArticles>({
      url: `/api/front/articles/getRecommendArticleById/${id}`,
    })
  }

  static blogTimelineGetArticleList(data?: object) {
    return request.post<ArticleListResponse>({
      url: '/api/front/articles/getTimeLineArticle',
      data
    })
  }
}
