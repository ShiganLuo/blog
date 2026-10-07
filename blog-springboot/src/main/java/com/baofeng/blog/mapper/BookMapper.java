package com.baofeng.blog.mapper;

import com.baofeng.blog.dto.front.BookDTO.*;
import com.baofeng.blog.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BookMapper {

    List<BookListResponse> selectBooks(@Param("onlyPublished") boolean onlyPublished);

    BookDetailResponse selectBookById(@Param("id") Long id, @Param("onlyPublished") boolean onlyPublished);

    List<ChapterItem> selectChapters(@Param("bookId") Long bookId, @Param("onlyPublished") boolean onlyPublished);

    int insertBook(Book book);

    int updateBook(Book book);

    int softDeleteBook(@Param("id") Long id);

    int insertBookArticle(@Param("bookId") Long bookId,
                          @Param("articleId") Long articleId,
                          @Param("sortOrder") Integer sortOrder);

    int deleteBookArticle(@Param("bookId") Long bookId, @Param("articleId") Long articleId);

    int updateChapterOrder(@Param("bookId") Long bookId,
                           @Param("articleId") Long articleId,
                           @Param("sortOrder") Integer sortOrder);

    int updateChapterTitle(@Param("bookId") Long bookId,
                           @Param("articleId") Long articleId,
                           @Param("chapterTitle") String chapterTitle);

    List<Long> selectChapterArticleIds(@Param("bookId") Long bookId);

    Long selectMaxSortOrder(@Param("bookId") Long bookId);

    int countBookArticles(@Param("bookId") Long bookId, @Param("articleId") Long articleId);
}
