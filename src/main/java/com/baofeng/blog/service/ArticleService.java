package com.baofeng.blog.service;

import com.baofeng.blog.entity.Article;
import com.baofeng.blog.vo.admin.AdminArticleVO.*;
import com.baofeng.blog.vo.common.Article.ArticlePageResponseVO;
import com.baofeng.blog.vo.common.Article.ArticleVO;
import com.baofeng.blog.vo.front.FrontArticleVO.ArticleDetailResponse;
import com.baofeng.blog.vo.front.FrontArticleVO.ArticleDetailResponsePair;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface ArticleService {
    Long createArticle(CreateArticleRequest articleRequest) throws Exception;
    boolean deleteArticle(Long id);
    Article getArticleById(Long id);
    boolean updateArticleSelective(Article article);
    boolean updatePinStaus(Long id,boolean isPinned);

    /**
     * 查询文章列表，包括标签，分类，作者
     * @param ArticlePageRequestVO
     * @return ArticlePageResponseVO
     */
    ArticlePageResponseVO getArticlePage(ArticlePageRequestVO request);

    /**
     * 根据id查询文章信息，包括标签，分类，作者
     * @param articleId
     * @return
     */
    ArticleVO getArticlePageFormById(Long id);

    /**
     * 获取文章喜欢数
     * @param id
     * @return
     */
    Long getLikesById(Long id);
    
    boolean publishArticle(Long articleId,Long authorId);
    boolean isTitleExist(String title);

    /**
     * 上传文章封面
     * @param imageFile
     * @param articleId
     * @return
     * @throws IOException
     */
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

    /**
     * 统计文章总数
     * @return int
     */
    Long countAllArticles();

    /**
     * 根据文章发布时间计算上一篇文章和下一篇文章
     * @param id
     * @return ArticleDetailResponsePair
     */
    ArticleDetailResponsePair getPNArticleById(Long id);

    /**
     * 获取推荐文章
     * @param id
     * @return List<>
     */
    List<ArticleDetailResponse> getRecommendedArticles(Long id);
}
