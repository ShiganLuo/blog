export interface Article {
  id: string;
  articleCover: string;
  articleTitle: string;
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
  originUrl: string;
  thumbsUpTimes: number;
  authorId: number;
  articleContent: string;
  articleCover?: string;
  articleTitle?: string;
  articleDescription?: string;
  createdAt?: string;
  updatedAt?: string;
  categoryNameList?: string[];
  tagNameList?: string[];
  viewTimes?: number;
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
