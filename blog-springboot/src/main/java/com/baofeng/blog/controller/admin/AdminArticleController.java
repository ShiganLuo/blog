package com.baofeng.blog.controller.admin;
import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.admin.AdminArticleDTO.*;
import com.baofeng.blog.dto.common.ImageDTO.UploadImage;
import com.baofeng.blog.service.ArticleService;

import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import com.fasterxml.jackson.databind.JsonNode;


@RestController
@RequestMapping("/api/admin/articles")
@Validated
public class AdminArticleController {
    private final ArticleService articleService;

    public AdminArticleController (
        ArticleService articleService
    ) {
        this.articleService = articleService;
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
        return articleService.publishArticle(articleId,authorId);
        
    }
    /**
     * 根据id删除文章
     * @param id 文章id
     * @return 分页结果
     */
    @DeleteMapping("/deleteArticleById/{id}")
    public ApiResponse<String> deleteArticle(@PathVariable Long id){
        return articleService.deleteArticle(id);
    }

    /**
     * 批量逻辑删除文章
     * @param deleteArticlesLogicallyRequest
     * @return
     */
    @PostMapping("/updateArticlesDeletedStatus")
    public ApiResponse<String> updateArticlesDeletedStatus(@RequestBody DeleteArticlesLogicallyRequest deleteArticlesLogicallyRequest) {
        return articleService.deleteArticlesLogically(deleteArticlesLogicallyRequest);
    }
    /**
     * 根据id获取文章
     * @param id 文章id
     * @return 分页结果
     */
    @GetMapping("/getArticleById/{id}")
    public ApiResponse<AdminArticle> getArticleById(@PathVariable Long id){
        return articleService.getAdminArticleById(id);
    }

    /**
     * 更新文章（作者信息是否可以更新？）
     * @param article 文章
     * @return 分页结果
     */
    @PostMapping("/createOrupdateArticles")
    public ApiResponse<String> createOrupdateArticles(@RequestBody CreateOrupdateArticlesRequest request){
        return articleService.createOrupdateArticles(request);

    }

    /**
     * 文章是否置顶
     * @param id 文章id
     * @param isPinned 文章是否置顶
     * @return 分页结果
     */
    @PutMapping("/isTop/{id}/{isPinned}")
    public ApiResponse<String> updataPinStaus(@PathVariable Long id,@PathVariable boolean isPinned){
        return articleService.updatePinStaus(id,isPinned);
    }

    /**
     * 分页查询文章列表
     * @param request 分页查询参数
     * @return 分页结果
     */
    @PostMapping("/getArticleList")
    public ApiResponse<AdminArticlePageVO> getArticleList(@RequestBody CreateAdminArticlePageRequest createAdminArticlePageRequest) {
        return articleService.getAdminArticleList(createAdminArticlePageRequest);
    }

    /**
     * 判断文章标题是否重复
     * @param title 文章标题
     * @return 分页结果
     */
    @PostMapping("/titleExist")
    public ApiResponse<String> titleExist(@RequestBody String title) {
        return articleService.isTitleExist(title);

    }

    /**
     * 封面图片上传接口
     * @param file 图片文件
     * @return 图片URL/路径
     */
    @PostMapping("/uploadCover")
    public ApiResponse<String> uploadCover(@Validated @ModelAttribute UploadImage uploadImage) {
        return articleService.storeImage(uploadImage);
    }

}


    
    

