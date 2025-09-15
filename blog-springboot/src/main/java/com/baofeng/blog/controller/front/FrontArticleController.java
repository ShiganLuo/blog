package com.baofeng.blog.controller.front;

import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.admin.AdminArticleVO.ArticlePageRequestVO;
import com.baofeng.blog.vo.common.Article.ArticlePageResponseVO;
import com.baofeng.blog.vo.front.FrontArticleVO.*;
import com.baofeng.blog.service.ArticleService;

import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/front/articles")
@RequiredArgsConstructor
@Validated
public class FrontArticleController {
    
    private final ArticleService articleService;

    /**
     * 分页查询文章列表
     * @param request 分页查询参数
     * @return 分页结果
     */
    @PostMapping("/getArticleList")
    public ApiResponse<ArticlePageResponseVO> getArticlePage(@Validated @RequestBody ArticlePageRequestVO request) {
        // 参数校验

        if (request.sortBy() != null && !isValidSortField(request.sortBy())) {
            return ApiResponse.error(400, "无效的排序字段");
        }
        if (request.sortOrder() != null && !isValidSortOrder(request.sortOrder())) {
            return ApiResponse.error(400, "无效的排序方向");
        }
        return articleService.getArticlePage(request);
    }
    
    /**
     * 验证排序字段是否有效
     */
    private boolean isValidSortField(String sortBy) {
        return sortBy == null || sortBy.matches("^(createTime|updateTime|viewCount|likeCount|commentCount)$");
    }

    /**
     * 验证排序方向是否有效
     */
    private boolean isValidSortOrder(String sortOrder) {
        return sortOrder == null || sortOrder.equalsIgnoreCase("asc") || sortOrder.equalsIgnoreCase("desc");
    }

    @GetMapping("/getLikesById/{id}")
    public ApiResponse<Long> getLikesById(@PathVariable Long id) {
        return articleService.getLikesById(id);

    }

    /**
     * 根据id获取文章
     * @param id 文章id
     * @return 分页结果
     */
    @GetMapping("/getArticleById/{id}")
    public ApiResponse<ArticleDetailResponse> getArticleById(@PathVariable Long id){
        return articleService.getArticlePageFormById(id);
    }

    /**
     * 根据ID获取文章的上一篇、下一篇以及推荐文章（以创建时间为准）。推荐文章功能待完善
     */
    @GetMapping("/getRecommendArticleById/{id}")
    public ApiResponse<ArticleRecommendResponse> getRecommendArticleById(@PathVariable Long id){
        return articleService.getRecommendArticleById(id);
    }

    @PostMapping("/getTimeLineArticle")
    public ApiResponse<ArticleAbstractsResponse> getTimeLineArticle(TimeLineRequest request){
        return articleService.getTimeLine(request);
    }

    /**
     * 根据分类ID获取文章列表
     * @param request
     * @return
     */
    @PostMapping("/getArticlesByCategoryId")
    public ApiResponse<ArticleAbstractsResponse> getArticlesByCategoryId(@RequestBody CategoryOrTagRequest request) {
        return articleService.getArticlesByCategoryId(request);
    }

    /**
     * 根据标签ID获取文章列表
     * @param request
     * @return
     */
    @PostMapping("/getArticlesByTagId")
    public ApiResponse<ArticleAbstractsResponse> getArticlesByTagId(@RequestBody CategoryOrTagRequest request) {
        return articleService.getArticlesByTagId(request); 
    }
    
}
