export interface Article {
  id: string;
  article_cover: string;
  article_title: string;
  createdAt: string;
}
export type ArticleListResponse = {
    list: Article[];
    total: number;
}
export interface homeGetArticleListParam {
  pageNum: number;
  pageSize: number;
  keyword?: string;
  sortBy?: string;
  sortOrder?: string;
}

export interface ArticleInfo {
  id: number;
  authorName: string;
  type: number;
  origin_url: string;
  thumbs_up_times: number;
  author_id: number;
  article_content: string;
  article_cover?: string;
  article_title?: string;
  article_description?: string;
  createdAt?: string;
  updatedAt?: string;
  categoryNameList?: string[];
  tagNameList?: string[];
  view_times?: number;
  reading_duration?: number;
  is_top: number;
}
export type ArticleInfoListResponse = {
    total: number,
    list: ArticleInfo[]
} 
export type recommendArticles = {
    previous: ArticleInfo,
    next: ArticleInfo,
    recommend: ArticleInfo[]
}

export interface ArchiveGroup {
  year: number;
  articleList: Article[];
}
export type AritcleInfoLenghtList = {
  length:number,
  
}

export interface ArticleSearch   {
  id: number;
  article_title: string;
  highlight_content: string;
  rest_content: string;
  article_content: string
}
