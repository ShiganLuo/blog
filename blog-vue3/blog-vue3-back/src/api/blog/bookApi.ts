import request from '@/utils/http'

export interface BookListItem {
  id: number
  title: string
  subtitle?: string
  description?: string
  coverImage?: string
  status: number
  chapterCount: number
  createdAt?: string
  updatedAt?: string
}

export interface ChapterItem {
  articleId: number
  chapterTitle: string
  articleTitle: string
  sortOrder: number
}

export interface BookDetail {
  id: number
  title: string
  subtitle?: string
  description?: string
  coverImage?: string
  status: number
  chapters: ChapterItem[]
}

// 书架（管理端）
class BookAdminService {
  static listBook() {
    return request.get({ url: '/admin/book/list' })
  }

  static getBook(id: number) {
    return request.get({ url: `/admin/book/${id}` })
  }

  static addBook(data: { title: string; subtitle?: string; description?: string; coverImage?: string }) {
    return request.post({ url: '/admin/book/add', data })
  }

  static updateBook(data: {
    id: number
    title?: string
    subtitle?: string
    description?: string
    coverImage?: string
    status?: number
  }) {
    return request.put({ url: '/admin/book/update', data })
  }

  static deleteBook(id: number) {
    return request.del({ url: `/admin/book/delete/${id}` })
  }

  static addArticles(data: { bookId: number; articleIds: number[] }) {
    return request.post({ url: '/admin/book/addArticles', data })
  }

  static removeArticle(bookId: number, articleId: number) {
    return request.post({ url: '/admin/book/removeArticle', data: { bookId, articleId } })
  }

  static reorder(bookId: number, orderedArticleIds: number[]) {
    return request.post({ url: '/admin/book/reorder', data: { bookId, orderedArticleIds } })
  }

  static renameChapter(bookId: number, articleId: number, chapterTitle: string) {
    return request.put({ url: '/admin/book/renameChapter', data: { bookId, articleId, chapterTitle } })
  }

  // 导出 ZIP（返回 Blob，配合 downloadExcel 使用）
  static exportBook(id: number) {
    return request.get({ url: `/admin/book/export/${id}`, responseType: 'blob' })
  }
}

export default BookAdminService
