package com.baofeng.blog.controller.front;

import org.springframework.web.bind.annotation.*;

import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.admin.AdminArticleVO.ArticlePageRequestVO;
import com.baofeng.blog.vo.common.Article.ArticlePageResponseVO;
import org.springframework.validation.annotation.Validated;
import lombok.RequiredArgsConstructor;

import com.baofeng.blog.service.ArticleService;

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

    
}
