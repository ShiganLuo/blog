package com.baofeng.blog.mapper;
import com.baofeng.blog.dto.admin.AdminArticleDTO.*;
import com.baofeng.blog.dto.front.FrontArticleDTO.*;
import com.baofeng.blog.entity.Article;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ArticleMapper {
    /**
     * 分页查询文章列表，包括作者，标签和分类
     * @param request 分页查询参数
     * @return 文章列表
     */
    List<FrontArticle> getFrontArticles(ArticlePageRequest articlePageRequest);
    
    /**
     * 根据id查询前端文章视图
     * @param articleId
     * @return
     */
    FrontArticle getFrontArticleById(Long articleId);

    /**
     * 分页查询文章列表1
     * @param createAdminArticlePageRequest
     * @return
     */
    List<AdminArticle> getAdminArticlePage(CreateAdminArticlePageRequest createAdminArticlePageRequest);
    
    /**
     * 根据id查询文章信息，包括作者，标签和分类
     * @param id
     * @return
     */
    AdminArticle getAdminArticleById(Long articleId);
    /**
     * 查询某篇文章的上一篇文章
     * @param id
     * @return FrontArticle
     */
    FrontArticle getPrevArticle(Long id);

    /**
     * 
     * @param id
     * @return FrontArticle
     */
    FrontArticle getNextArticle(Long id);
    
    /**
     * 获得推荐文章
     * @param id
     * @return
     */
    List<FrontArticle> getRecommendedArticles(Long id);

    /**
     * 获取文章like数
     * @param id
     * @return
     */
    Long getLikesById(Long id);

    /**
     * 增加文章点赞数
     * @param id
     * @return
     */
    int incrementLikeById(Long id);

    /**
     * 减少文章点赞数
     * @param id
     * @return
     */
    int decreaseLikeById(Long id);

    Article getArticleById(Long id);
    
    int insertArticle(Article article);
    int updateArticle(Article article);
    int deleteArticle(Long id);
    /**
     * 选择性更新文章
     * @param article
     * @return
     */
    int updateArticleSelective(Article article);

    Long getAuthorIdById(Long articleId);
    boolean isTitleExist(String title);
    Long countAllArticles();

    /**
     * 取消文章点赞
     */
    int decrementLikesById(Long id);

    /**
     * 检查文章是否存在
     * @param id
     * @return
     */
    Boolean selectCountById(Long id);

    /**
     * 获取按文章创建时间排序的对象
     * @return
     */
    List<ArticleAbstractResponse> selectArticleOrderedByCreatedAt();

    /**
     * 根据标签ID查询文章
     * @param tagId
     * @return
     */
    List<ArticleAbstractResponse> selectArticlesByTagId(Long tagId);

    /**
     * 根据分类ID查询文章
     * @param categoryId
     * @return
     */
    List<ArticleAbstractResponse> selectArticlesByCategoryId(Long categoryId);

    /**
     * 查看指定时间已经存在的文章
     * @param createdAt
     * @return
     */
    long selectArticleCountWhenSpecifiedTime(@Param("createdAt") LocalDateTime createdAt);
}
