package com.baofeng.blog.service;

import com.baofeng.blog.entity.Article;
import com.baofeng.blog.vo.admin.AdminArticleCRUDVO.*;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface ArticleService {
    Long createArticle(CreateArticleRequest articleRequest) throws Exception;
    boolean deleteArticle(Long id);
    Article getArticleById(Long id);
    boolean updateArticleSelective(Article article);
    boolean updatePinStaus(Long id,boolean isPinned);
    ArticlePageResponseVO getArticlePage(ArticlePageRequestVO request);
    boolean publishArticle(Long articleId,Long authorId);
    boolean isTitleExist(String title);
    String storeImage(MultipartFile imageFile,Long articleId) throws IOException;
    /**
     * 设置文章分类
     * @param CategoryRequest
     * @return boolean
     */
    boolean addCategory(CategoryRequest request);
    /**
     * 设置文章标签
     * @param TagRequest
     * @return boolean
     */
    boolean addTag(TagRequest request);

}
