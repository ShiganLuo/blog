import request from "@/utils/http/index";

export interface BookChapter {
  articleId: number;
  chapterTitle: string;
  articleTitle: string;
  sortOrder: number;
}

export interface BookListItem {
  id: number;
  title: string;
  subtitle?: string;
  description?: string;
  coverImage?: string;
  status: number;
  chapterCount: number;
}

export interface BookDetail {
  id: number;
  title: string;
  subtitle?: string;
  description?: string;
  coverImage?: string;
  status: number;
  chapters: BookChapter[];
}

export interface ChapterRef {
  articleId: number;
  chapterTitle: string;
}

export interface ChapterResult {
  bookId: number;
  bookTitle: string;
  chapterIndex: number;
  totalChapters: number;
  content: any;
  prev: ChapterRef | null;
  next: ChapterRef | null;
}

export class BookService {
  // 上架的书列表
  static getBookList() {
    return request.get<BookListItem[]>({
      url: "/front/book/list",
    });
  }

  // 书详情 + 章节目录
  static getBookById(id: number | string) {
    return request.get({ url: `/front/book/${id}` });
  }

  // 章正文 + 上下章
  static getChapter(bookId: number | string, articleId: number | string) {
    return request.get({ url: `/front/book/${bookId}/chapter/${articleId}` });
  }

  // 导出 ZIP 直链（前台接口免登录，可直接 <a> 下载）
  static exportUrl(bookId: number | string) {
    const base = import.meta.env.VITE_API_BASE_URL || "/api";
    return `${base}/front/book/export/${bookId}`;
  }
}
