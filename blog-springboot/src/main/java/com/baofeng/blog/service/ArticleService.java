package com.baofeng.blog.service;

import com.baofeng.blog.entity.Article;
import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.admin.AdminArticleVO.*;
import com.baofeng.blog.vo.common.Image.UploadImage;
import com.baofeng.blog.vo.front.FrontArticleVO.*;

import java.io.IOException;


public interface ArticleService {
    
    /**
     * 查询文章列表，包括标签，分类，作者
     * @param ArticlePageRequestVO
     * @return ArticlePageResponseVO
     */
    public ApiResponse<ArticlePageResponseVO> getArticlePage(ArticlePageRequestVO request);

    /**
     * 查询后台文章列表
     * @param createAdminArticlePageRequest
     * @return
     */
    public ApiResponse<AdminArticlePageVO> getAdminArticlePage(CreateAdminArticlePageRequest createAdminArticlePageRequest);

    /**
     * 创建文章
     * @param articleRequest
     * @return
     * @throws Exception
     */
    public ApiResponse<Long> createArticle(CreateArticleRequest articleRequest);

    /**
     * 删除文章
     * @param id
     * @return
     */
    public ApiResponse<String> deleteArticle(Long id);

    /**
     * 根据id获取文章
     * @param id
     * @return
     */
    public ApiResponse<Article> getArticleById(Long id);

    /**
     * 更新文章
     * @param article
     * @return
     */
    public ApiResponse<String> updateArticleSelective(Article article);

    /**
     * 更新文章置顶状态
     * @param id
     * @param isPinned
     * @return
     */
    public ApiResponse<String> updatePinStaus(Long id,boolean isPinned);



    /**
     * 根据id查询文章信息，包括标签，分类，作者
     * @param articleId
     * @return
     */
    public ApiResponse<ArticleDetailResponse> getArticlePageFormById(Long id);

    /**
     * 获取文章喜欢数
     * @param id
     * @return
     */
    public ApiResponse<Long> getLikesById(Long id);
    
    /**
     * 发表文章
     * @param articleId
     * @param authorId
     * @return
     */
    public ApiResponse<String> publishArticle(Long articleId,Long authorId);

    /**
     * 判断文章标题是否存在
     * @param title
     * @return
     */
    public ApiResponse<String> isTitleExist(String title);

    /**
     * 上传文章封面
     * @param imageFile
     * @param articleId
     * @return
     * @throws IOException
     */
    public ApiResponse<String> storeImage(UploadImage uploadImage);

    /**
     * 设置文章分类
     * @param CategoryRequest
     * @return boolean
     */
    public ApiResponse<String> addCategory(CategoryRequest request);

    /**
     * 设置文章标签
     * @param TagRequest
     * @return boolean
     */
    public ApiResponse<String> addTag(TagRequest request);

    /**
     * 统计文章总数
     * @return int
     */
    Long countAllArticles();

    /**
     * 获取推荐文章
     * @param id
     * @return List<>
     */
    public ApiResponse<ArticleRecommendResponse> getRecommendArticleById(Long id);

    /**
     * 输出按创建顺序排列的文章
     * @param request
     * @return
     */
    public ApiResponse<ArticleAbstractsResponse> getTimeLine(TimeLineRequest request);

    /**
     * 根据标签ID查询文章
     * @param tagId
     * @return
     */
    public ApiResponse<ArticleAbstractsResponse> getArticlesByTagId(CategoryOrTagRequest request);

    /**
     * 根据分类ID查询文章
     * @param categoryId
     * @return
     */
    public ApiResponse<ArticleAbstractsResponse> getArticlesByCategoryId(CategoryOrTagRequest request);
}
