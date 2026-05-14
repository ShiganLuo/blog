import request from "@/utils/http/index";
import { type ArticleListResponse,
        type ArticleInfo,
        type recommendArticles,
        type ArticleInfoListResponse,
        type ArticleSearch
} from "@/types/blog/article";


export class ArticleService {

  static addArticleViews(articleId:number) {
    return request.post<string>({
      url: '/front/articles/addArticleViews',
      data: articleId
    })
  }

  static homeGetArticleList(data?: object) {
    return request.post<ArticleInfoListResponse>({
        url: '/front/articles/getArticleList',
        data
    })
  }

  // TODO: 后端暂未实现热门文章接口，待实现后取消注释
  // static getHotArticle(data?: object) {
  //   return request.post<ArticleInfo[]>({
  //     url: '/front/articles/getHotArticle',
  //     data
  //   })
  // }

  // TODO: 后端暂未实现搜索文章接口，待实现后取消注释
  // static getArticleByContent(data?: object) {
  //   return request.post<ArticleSearch[]>({
  //     url: '/front/articles/searchByContent',
  //     data
  //   })
  // }

  static getArticleById(id?: string | number) {
    if (id == null) {
      throw new Error('getArticleById: id is required')
    }
    return request.get<ArticleInfo>({
      url: `/front/articles/getArticleById/${id}`,
    })
  }

  static getArticleListByTagId(data?: object) {
    return request.post<ArticleListResponse>({
      url: '/front/articles/getArticlesByTagId',
      data
    })
  }

  static getArticleListByCategoryId(data?: object) {
    return request.post<ArticleListResponse>({
      url: '/front/articles/getArticlesByCategoryId',
      data
    })
  }

  static getRecommendArticleById(id?: string | number) {
    return request.get<recommendArticles>({
      url: `/front/articles/getRecommendArticleById/${id}`,
    })
  }

  static blogTimelineGetArticleList(data?: object) {
    return request.post<ArticleListResponse>({
      url: '/front/articles/getTimeLineArticle',
      data
    })
  }
}
