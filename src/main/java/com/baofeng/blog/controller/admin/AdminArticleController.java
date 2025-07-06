package com.baofeng.blog.controller.admin;
import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.admin.AdminArticleCRUDVO.*;
import com.baofeng.blog.entity.Article;
import com.baofeng.blog.service.ArticleService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.validation.annotation.Validated;
import com.fasterxml.jackson.databind.JsonNode;

@RestController
@RequestMapping("/api/admin/articles")
@RequiredArgsConstructor
@Validated
public class AdminArticleController {

    private final ArticleService articleService;
    /**
     * 发表文章
     * @param articleRequest 文章
     * @return 分页结果
     */
    @PostMapping("/create")
    public ApiResponse<Long> createArticle(@RequestBody CreateArticleRequest articleRequest) {
        try {
            Long articleId = articleService.createArticle(articleRequest);
            if ( articleId != null) {
                return ApiResponse.success(articleId);
            }else{
                return ApiResponse.error(400, "创建失败");
            }
        } catch (Exception e) {
            return ApiResponse.error(400, "创建失败: "+e.getMessage());
        }

    }
    /**
     * 发表文章
     * @param requestBody 文章id和作者id
     * @return 分页结果
     */
    @PostMapping("/publish")
    public ApiResponse<String> publishArticle(@RequestBody JsonNode requestBody) {
        Long articleId = requestBody.get("articleId").asLong(); // 从请求体中提取用户名
        Long authorId = requestBody.get("authorId").asLong(); // 提取新密码
        boolean success = articleService.publishArticle(articleId,authorId);
        if ( success ){
            return ApiResponse.success(null);
        } else {
            return ApiResponse.error(400, "发表失败");
        }
    }
    /**
     * 根据id删除文章
     * @param id 文章id
     * @return 分页结果
     */
    @DeleteMapping("/deleteArticleById/{id}")
    public ApiResponse<String> deleteArticle(@PathVariable Long id){
        boolean success = articleService.deleteArticle(id);
        if (success) {
            return ApiResponse.success(null);
        } else {
            return ApiResponse.error(404, "文章未找到");
        }
    }
    /**
     * 根据id获取文章
     * @param id 文章id
     * @return 分页结果
     */
    @GetMapping("/getArticleById/{id}")
    public ApiResponse<Article> getArticleById(@PathVariable Long id){
        Article article = articleService.getArticleById(id);
        if (article !=null ) {
            return ApiResponse.success(article);
        } else {
            return ApiResponse.error(404, "文章不存在");
        }
    }
    /**
     * 更新文章
     * @param article 文章
     * @return 分页结果
     */
    @PostMapping("/update")
    public ApiResponse<String> updateArticleSelective(@RequestBody Article article){
        if ( article.getId() == null){
            return ApiResponse.error(400,"文章id不能为空");
        }else {
            boolean success =  articleService.updateArticleSelective(article);
            if (success) {
                return ApiResponse.success(null);
            } else {
                return ApiResponse.error(400, "更新失败");
            }
        }
    }

    /**
     * 文章是否置顶
     * @param id 文章id
     * @param isPinned 文章是否置顶
     * @return 分页结果
     */
    @PutMapping("/isTop/{id}/{isPinned}")
    public ApiResponse<String> updataPinStaus(@PathVariable Long id,@PathVariable boolean isPinned){
        boolean success = articleService.updatePinStaus(id,isPinned);
        if ( success ) {
            return ApiResponse.success(null);
        } else {
            return ApiResponse.error(400, "文章状态更新失败");
        }
    }

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
     * 判断文章标题是否重复
     * @param title 文章标题
     * @return 分页结果
     */
    @PostMapping("/titleExist")
    public ApiResponse<String> titleExist(@RequestBody String title) {
        try {
            boolean isDuplicated = articleService.isTitleExist(title);
            if ( !isDuplicated ) {
                return ApiResponse.success("标题不存在");
            } else {
                return ApiResponse.error(400, "标题已存在");
            }
        } catch (Exception e){
            return ApiResponse.error(400, "错误："+ e.getMessage());

        }

    }
    /**
     * 封面图片上传接口
     * @param file 图片文件
     * @return 图片URL/路径
     */
    @PostMapping("/uploadCover")
    public ApiResponse<String> uploadCover(@RequestParam("file") MultipartFile file,@RequestParam("articleId") Long articleId) {
        try {
            String imageUrl = articleService.storeImage(file,articleId);
            return ApiResponse.success(imageUrl);
        } catch (Exception e) {
            return ApiResponse.error(500, "上传失败：" + e.getMessage());
        }
    }
    /**
     * 增加分类接口,如果表中没有则添加
     */
    @PostMapping("/uploadCategory")
    public ApiResponse<String> uploadCategory(@RequestBody CategoryRequest request){
        try {
            boolean success = articleService.addCategory(request);
            if ( success ) {
                return ApiResponse.success(null);
            } else {
                return ApiResponse.error(400, "文章分类设置失败");
            }
        } catch ( Exception e) {
            return ApiResponse.error(400, "文章分类设置失败："+ e.getMessage());
        }
    }
    /**
     * 增加标签接口,如果表中没有则添加
     */
    @PostMapping("/uploadTag")
    public ApiResponse<String> uploadTag(@RequestBody TagRequest request) {
        try {
            boolean success = articleService.addTag(request);
            if ( success ) {
                return ApiResponse.success(null);
            } else {
                return ApiResponse.error(400, "文章标签设置失败");
            }
        } catch ( Exception e ) {
            return ApiResponse.error(400, "文章标签设置失败" + e.getMessage());
        }
    }
}


    
    

