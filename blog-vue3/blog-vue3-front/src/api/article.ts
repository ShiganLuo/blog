import { fullRequest, request } from "./http/index";

interface homeGetArticleListParam {
  pageNum: number;
  pageSize: number;
  keyword?: string;
  sortBy?: string;
  sortOrder?: string;
}

interface Article {
  id: number;
  title: string;
  // 根据实际接口补充其他字段
}

// 由在axios配置中由BaseResponse包裹，只需要定义后端返回数据的result部分类型
interface homeGetArticleListResult {
  list: Article[];
  total: number;

}
export const homeGetArticleList = (data?: homeGetArticleListParam) => {
    return fullRequest<homeGetArticleListResult>({
        method: 'post',
        url: '/api/front/articles/getArticleList',
        data:data
    })
}


export const getHotArticle = (data?: object) => {
  return request({
    method: 'post',
    url: '',
    data: data
  })
}

export const getArticleByContent = (data?: object) => {
  return request({
    method: 'post',
    url: '',
    data: data
  })
}


export const getArticleById = (id?: string | number) => {
  if (id == null) {
    throw new Error('getArticleById: id is required')
  }
  return fullRequest({
    method:  'get',
    url: `/api/front/articles/getArticleById/${id}`,
  })
}

export const getArticleListByTagId = (data?: object) => {
  return fullRequest({
    method:  'post',
    url: '/api/front/articles/getArticlesByTagId',
    data: data
  })
}

export const getArticleListByCategoryId = (data?: object) => {
  return fullRequest({
    method:  'post',
    url: '/api/front/articles/getArticlesByCategoryId',
    data: data
  })
}

export const getRecommendArticleById = (id?: string | number) => {
  return fullRequest({
    method:  'get',
    url: `/api/front/articles/getRecommendArticleById/${id}`,
  })
}

export const readingDuration = (data?: object) => {
  return fullRequest({
    method:  'post',
    url: '',
    data: data
  })
}

export const blogTimelineGetArticleList = (data?: object) => {
  return fullRequest({
    method:  'post',
    url: '/api/front/articles/getTimeLineArticle',
    data: data
  })
}




