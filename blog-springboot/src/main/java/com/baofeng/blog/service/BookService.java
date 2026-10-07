package com.baofeng.blog.service;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.front.BookDTO.*;

import java.util.List;

public interface BookService {

    /** 书列表（onlyPublished=true 仅上架，用于前台） */
    ApiResponse<List<BookListResponse>> getBooks(boolean onlyPublished);

    /** 书详情 + 章节目录 */
    ApiResponse<BookDetailResponse> getBookById(Long id, boolean onlyPublished);

    /** 章正文 + 上下章 */
    ApiResponse<ChapterResponse> getChapter(Long bookId, Long articleId, boolean onlyPublished);

    ApiResponse<String> addBook(AddBookRequest request);

    ApiResponse<String> updateBook(UpdateBookRequest request);

    ApiResponse<String> deleteBook(Long id);

    ApiResponse<String> addArticles(AddArticlesRequest request);

    ApiResponse<String> removeArticle(RemoveArticleRequest request);

    ApiResponse<String> reorder(ReorderRequest request);

    ApiResponse<String> renameChapter(RenameChapterRequest request);
}
