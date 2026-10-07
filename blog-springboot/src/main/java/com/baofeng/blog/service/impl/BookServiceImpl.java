package com.baofeng.blog.service.impl;

import com.baofeng.blog.common.util.ArticleConvertUtil;
import com.baofeng.blog.common.util.UrlNormalizeUtil;
import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.front.BookDTO.*;
import com.baofeng.blog.dto.front.FrontArticleDTO.ArticleDetailResponse;
import com.baofeng.blog.dto.front.FrontArticleDTO.FrontArticle;
import com.baofeng.blog.entity.Article;
import com.baofeng.blog.entity.Book;
import com.baofeng.blog.enums.ResultCodeEnum;
import com.baofeng.blog.mapper.ArticleMapper;
import com.baofeng.blog.mapper.BookMapper;
import com.baofeng.blog.mapper.UserMapper;
import com.baofeng.blog.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookMapper bookMapper;
    private final ArticleMapper articleMapper;
    private final UserMapper userMapper;

    /** 获取当前登录用户的ID（管理端创建书时用） */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        String username = authentication.getName();
        if (username == null || "anonymousUser".equals(username)) {
            return null;
        }
        return userMapper.getIdByUsername(username);
    }

    @Override
    public ApiResponse<List<BookListResponse>> getBooks(boolean onlyPublished) {
        return ApiResponse.success(bookMapper.selectBooks(onlyPublished));
    }

    @Override
    public ApiResponse<BookDetailResponse> getBookById(Long id, boolean onlyPublished) {
        BookDetailResponse book = bookMapper.selectBookById(id, onlyPublished);
        if (book == null) {
            return ApiResponse.error(ResultCodeEnum.NOT_FOUND, "书不存在");
        }
        book.setChapters(bookMapper.selectChapters(id, onlyPublished));
        return ApiResponse.success(book);
    }

    @Override
    public ApiResponse<ChapterResponse> getChapter(Long bookId, Long articleId, boolean onlyPublished) {
        BookDetailResponse book = bookMapper.selectBookById(bookId, onlyPublished);
        if (book == null) {
            return ApiResponse.error(ResultCodeEnum.NOT_FOUND, "书不存在");
        }
        List<ChapterItem> chapters = bookMapper.selectChapters(bookId, onlyPublished);
        int index = -1;
        for (int i = 0; i < chapters.size(); i++) {
            if (chapters.get(i).getArticleId().equals(articleId)) {
                index = i;
                break;
            }
        }
        if (index < 0) {
            return ApiResponse.error(ResultCodeEnum.NOT_FOUND, "章节不存在");
        }
        FrontArticle frontArticle = articleMapper.getFrontArticleById(articleId);
        if (frontArticle == null) {
            return ApiResponse.error(ResultCodeEnum.NOT_FOUND, "文章不存在");
        }
        ArticleDetailResponse content = ArticleConvertUtil.convertToDetailResponse(frontArticle);

        ChapterResponse response = new ChapterResponse();
        response.setBookId(bookId);
        response.setBookTitle(book.getTitle());
        response.setChapterIndex(index + 1);
        response.setTotalChapters(chapters.size());
        response.setContent(content);
        response.setPrev(index > 0 ? toRef(chapters.get(index - 1)) : null);
        response.setNext(index < chapters.size() - 1 ? toRef(chapters.get(index + 1)) : null);
        return ApiResponse.success(response);
    }

    private ChapterRef toRef(ChapterItem item) {
        ChapterRef ref = new ChapterRef();
        ref.setArticleId(item.getArticleId());
        ref.setChapterTitle(item.getChapterTitle());
        return ref;
    }

    @Override
    @Transactional
    public ApiResponse<String> addBook(AddBookRequest request) {
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST, "书名不能为空");
        }
        Long authorId = getCurrentUserId();
        if (authorId == null) {
            return ApiResponse.error(ResultCodeEnum.UNAUTHORIZED, "请先登录");
        }
        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setSubtitle(request.getSubtitle());
        book.setDescription(request.getDescription());
        book.setCoverImage(UrlNormalizeUtil.stripUrlPrefix(request.getCoverImage()));
        book.setAuthorId(authorId);
        book.setStatus(0);
        book.setIsDeleted(false);
        bookMapper.insertBook(book);
        return book.getId() != null
                ? ApiResponse.success("书创建成功")
                : ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR, "书创建失败");
    }

    @Override
    @Transactional
    public ApiResponse<String> updateBook(UpdateBookRequest request) {
        if (request.getId() == null) {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST, "id不能为空");
        }
        Book book = new Book();
        book.setId(request.getId());
        book.setTitle(request.getTitle());
        book.setSubtitle(request.getSubtitle());
        book.setDescription(request.getDescription());
        book.setCoverImage(UrlNormalizeUtil.stripUrlPrefix(request.getCoverImage()));
        book.setStatus(request.getStatus());
        int rows = bookMapper.updateBook(book);
        return rows > 0
                ? ApiResponse.success("书更新成功")
                : ApiResponse.error(ResultCodeEnum.NOT_FOUND, "书不存在");
    }

    @Override
    @Transactional
    public ApiResponse<String> deleteBook(Long id) {
        int rows = bookMapper.softDeleteBook(id);
        return rows > 0
                ? ApiResponse.success("书删除成功")
                : ApiResponse.error(ResultCodeEnum.NOT_FOUND, "书不存在");
    }

    @Override
    @Transactional
    public ApiResponse<String> addArticles(AddArticlesRequest request) {
        if (request.getBookId() == null || request.getArticleIds() == null || request.getArticleIds().isEmpty()) {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST, "参数不完整");
        }
        if (bookMapper.selectBookById(request.getBookId(), false) == null) {
            return ApiResponse.error(ResultCodeEnum.NOT_FOUND, "书不存在");
        }
        List<Long> existing = bookMapper.selectChapterArticleIds(request.getBookId());
        Set<Long> existingSet = new HashSet<>(existing);
        long sort = bookMapper.selectMaxSortOrder(request.getBookId());
        int added = 0;
        for (Long articleId : new LinkedHashSet<>(request.getArticleIds())) {
            if (existingSet.contains(articleId)) {
                continue;
            }
            Article article = articleMapper.getArticleById(articleId);
            if (article == null || Boolean.TRUE.equals(article.getIsDeleted())) {
                return ApiResponse.error(ResultCodeEnum.NOT_FOUND, "文章不存在: " + articleId);
            }
            bookMapper.insertBookArticle(request.getBookId(), articleId, (int) (++sort));
            added++;
        }
        return ApiResponse.success("已加入 " + added + " 个章节");
    }

    @Override
    @Transactional
    public ApiResponse<String> removeArticle(RemoveArticleRequest request) {
        if (request.getBookId() == null || request.getArticleId() == null) {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST, "参数不完整");
        }
        int rows = bookMapper.deleteBookArticle(request.getBookId(), request.getArticleId());
        return rows > 0
                ? ApiResponse.success("章节已移除")
                : ApiResponse.error(ResultCodeEnum.NOT_FOUND, "章节不存在");
    }

    @Override
    @Transactional
    public ApiResponse<String> reorder(ReorderRequest request) {
        if (request.getBookId() == null || request.getOrderedArticleIds() == null) {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST, "参数不完整");
        }
        List<Long> current = bookMapper.selectChapterArticleIds(request.getBookId());
        Set<Long> currentSet = new HashSet<>(current);
        Set<Long> newSet = new HashSet<>(request.getOrderedArticleIds());
        if (currentSet.size() != newSet.size() || !currentSet.equals(newSet)) {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST, "排序列表与现有章节不一致");
        }
        int order = 0;
        for (Long articleId : request.getOrderedArticleIds()) {
            bookMapper.updateChapterOrder(request.getBookId(), articleId, ++order);
        }
        return ApiResponse.success("章节排序已更新");
    }

    @Override
    @Transactional
    public ApiResponse<String> renameChapter(RenameChapterRequest request) {
        if (request.getBookId() == null || request.getArticleId() == null) {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST, "参数不完整");
        }
        int rows = bookMapper.updateChapterTitle(request.getBookId(), request.getArticleId(),
                request.getChapterTitle() == null ? "" : request.getChapterTitle().trim());
        return rows > 0
                ? ApiResponse.success("章节名已更新")
                : ApiResponse.error(ResultCodeEnum.NOT_FOUND, "章节不存在");
    }
}
