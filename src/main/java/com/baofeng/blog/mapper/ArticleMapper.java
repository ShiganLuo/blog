package com.baofeng.blog.mapper;
import com.baofeng.blog.entity.Article;
import com.baofeng.blog.vo.admin.AdminArticleCRUDVO.*;
import org.apache.ibatis.annotations.Mapper;
// import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Locale.Category;
@Mapper
public interface ArticleMapper {
    /**
     * 分页查询文章列表
     * @param request 分页查询参数
     * @return 文章列表
     */
    List<ArticleVO> getArticlePage(ArticlePageRequestVO request);
    
    Article getArticleById(Long id);
    int insertArticle(Article article);
    int updateArticle(Article article);
    int deleteArticle(Long id);
    int updateArticleSelective(Article article);
    Long getAuthorIdById(Long articleId);
    boolean isTitleExist(String title);
}
