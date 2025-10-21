package com.baofeng.blog.controller.admin;
import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.admin.AdminArticleVO.*;
import com.baofeng.blog.vo.common.Image.UploadImage;
import com.baofeng.blog.entity.Article;
import com.baofeng.blog.service.ArticleService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
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
        return articleService.createArticle(articleRequest);
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
     * 根据id获取文章
     * @param id 文章id
     * @return 分页结果
     */
    @GetMapping("/getArticleById/{id}")
    public ApiResponse<Article> getArticleById(@PathVariable Long id){
        return articleService.getArticleById(id);
    }

    /**
     * 更新文章（作者信息是否可以更新？）
     * @param article 文章
     * @return 分页结果
     */
    @PostMapping("/update")
    public ApiResponse<String> updateArticleSelective(@RequestBody Article article){
        if ( article.getId() == null) {
            return ApiResponse.error(400,"文章id不能为空");
        }
        
        return articleService.updateArticleSelective(article);

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
    public ApiResponse<AdminArticlePageVO> getArticlePage(@RequestBody CreateAdminArticlePageRequest createAdminArticlePageRequest) {
        return articleService.getAdminArticlePage(createAdminArticlePageRequest);
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
    /**
     * 增加分类接口,如果表中没有则添加
     */
    @PostMapping("/uploadCategory")
    public ApiResponse<String> uploadCategory(@RequestBody CategoryRequest request){
        return articleService.addCategory(request);
    }
    /**
     * 增加标签接口,如果表中没有则添加
     */
    @PostMapping("/uploadTag")
    public ApiResponse<String> uploadTag(@RequestBody TagRequest request) {
        return articleService.addTag(request);
    }
}


    
    

