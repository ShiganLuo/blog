package com.baofeng.blog.controller.front;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.admin.AdminArticleDTO.ArticlePageRequest;
import com.baofeng.blog.dto.front.FrontArticleDTO.*;
import com.baofeng.blog.service.ArticleService;

import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;



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
    public ApiResponse<FrontArticlePageResponse> getArticlePage(@Validated @RequestBody ArticlePageRequest articlePageRequest) {
        // 参数校验

        if (articlePageRequest.sortBy() != null && !isValidSortField(articlePageRequest.sortBy())) {
            return ApiResponse.error(400, "无效的排序字段");
        }
        if (articlePageRequest.sortOrder() != null && !isValidSortOrder(articlePageRequest.sortOrder())) {
            return ApiResponse.error(400, "无效的排序方向");
        }
        return articleService.getArticlePage(articlePageRequest);
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
