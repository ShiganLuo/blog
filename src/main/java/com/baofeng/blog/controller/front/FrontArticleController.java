package com.baofeng.blog.controller.front;

import org.springframework.web.bind.annotation.*;

import com.baofeng.blog.entity.User;
import com.baofeng.blog.entity.Article;
import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.admin.AdminArticleVO.ArticlePageRequestVO;
import com.baofeng.blog.vo.common.Article.ArticlePageResponseVO;
import com.baofeng.blog.vo.common.Article.ArticleVO;
import com.baofeng.blog.vo.front.FrontArticleVO.LikeRequest;
import com.baofeng.blog.vo.front.FrontArticleVO.ArticleDetailResponse;
import org.springframework.validation.annotation.Validated;
import lombok.RequiredArgsConstructor;


import com.baofeng.blog.service.ArticleService;
import com.baofeng.blog.service.UserService;

@RestController
@RequestMapping("/api/front/articles")
@RequiredArgsConstructor
@Validated
public class FrontArticleController {
    
    private final ArticleService articleService;
    private final UserService userService;

    /**
     * 分页查询文章列表
     * @param request 分页查询参数
     * @return 分页结果
     */
    @PostMapping("/getArticleList")
    public ApiResponse<ArticlePageResponseVO> getArticlePage(@RequestBody ArticlePageRequestVO request) {
        // 参数校验
        if (request == null) {
            return ApiResponse.error(400, "请求参数不能为空");
        }
        if (request.pageNum() != null && request.pageNum() < 1) {
            return ApiResponse.error(400, "页码必须大于0");
        }
        if (request.pageSize() != null && request.pageSize() < 1) {
            return ApiResponse.error(400, "每页显示条数必须大于0");
        }
        if (request.sortBy() != null && !isValidSortField(request.sortBy())) {
            return ApiResponse.error(400, "无效的排序字段");
        }
        if (request.sortOrder() != null && !isValidSortOrder(request.sortOrder())) {
            return ApiResponse.error(400, "无效的排序方向");
        }
        
        try {
            return ApiResponse.success(articleService.getArticlePage(request));
        } catch (Exception e) {
            return ApiResponse.error(500, "查询失败：" + e.getMessage());
        }
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

    /**
     * 取消文章喜欢
     */
    // @PostMapping("/cancelLike")
    // public ApiResponse<String> cancelLike(@RequestBody likeRequest request){
    //     try {

    //     } catch (Exception e) {
    //         return ApiResponse.error(400, "取消失败");
    //     }
    // }

    /**
     * 根据id获取文章
     * @param id 文章id
     * @return 分页结果
     */
    @GetMapping("/getArticleById/{id}")
    public ApiResponse<ArticleDetailResponse> getArticleById(@PathVariable Long id){

        try {
            ArticleVO articleVO = articleService.getArticlePageFormById(id);
            ArticleDetailResponse articleDetailResponse = new ArticleDetailResponse();
            articleDetailResponse.setId(articleVO.getId());
            articleDetailResponse.setAuthorName(articleVO.getAuthor().getAuthorNickname());
            articleDetailResponse.setType(articleVO.getType());
            articleDetailResponse.setOrigin_url(articleVO.getOriginUrl());
            articleDetailResponse.setThumbs_up_times(articleVO.getThumbs_up_times());
            articleDetailResponse.setAuthor_id(articleVO.getAuthor().getAuthorId());
            articleDetailResponse.setArticle_content(articleVO.getArticle_content());
            articleDetailResponse.setArticle_cover(articleVO.getArticle_cover());
            articleDetailResponse.setArticle_title(articleVO.getArticle_title());
            articleDetailResponse.setView_times(articleVO.getView_times());
            articleDetailResponse.setCreatedAt(articleVO.getCreatedAt());
            articleDetailResponse.setUpdatedAt(articleVO.getUpdatedAt());
            articleDetailResponse.setCategoryNameList(articleVO.getCategoryNameList());
            articleDetailResponse.setTagNameList(articleVO.getTagNameList());
            return ApiResponse.success(articleDetailResponse);
        } catch (Exception e) {
            return ApiResponse.error(404, "文章不存在" + e.getMessage());
        }

    }


    
}
