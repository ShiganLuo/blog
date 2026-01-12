package com.baofeng.blog.controller.admin;

import com.baofeng.blog.service.TagService;
import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.admin.AdminArticleDTO.TagRequest;
import com.baofeng.blog.dto.admin.AdminTagPageDTO.CreateTagRequest;
import com.baofeng.blog.dto.admin.AdminTagPageDTO.TagPageRequestVO;
import com.baofeng.blog.dto.admin.AdminTagPageDTO.TagPageResponseVO;
import com.baofeng.blog.dto.common.TagDTO.TagDictionaryResponse;
import com.baofeng.blog.service.ArticleService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/api/admin/tags")
public class AdminTagController {
    
    private final TagService tagService;
    private final ArticleService articleService;

    public AdminTagController (
        TagService tagService,
        ArticleService articleService
    ) {
        this.tagService = tagService;
        this.articleService = articleService;
    }
    /**
     * 创建标签
     * @param request 创建标签请求
     * @return 创建结果
     */
    @PostMapping("/create")
    public ApiResponse<String> createTag(@RequestBody @Validated CreateTagRequest request) {        
        return tagService.createTag(request);
    }

    /**
     * 删除标签
     * @param id 标签ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteTag(@PathVariable Long id) {
        return tagService.deleteTag(id);
    }

    /**
     * 分页查询标签列表
     * @param request 分页查询参数
     * @return 分页结果
     */
    @PostMapping("/list")
    public ApiResponse<TagPageResponseVO> getTagPage(@RequestBody @Validated TagPageRequestVO request) {
        return tagService.getTagPage(request);
    }
    /**
     * 查询标签列表
     * @return {id,name}
     */
    @GetMapping("/getTagDictionary")
    public ApiResponse<List<TagDictionaryResponse>> getTagDictionary(
        @RequestParam(required = false) String keyword
    ){
        return tagService.getTagDictionary(keyword); // 注意返回修改了，记得修改前端
    }
    /**
     * 增加标签接口,如果表中没有则添加
     */
    @PostMapping("/uploadTag")
    public ApiResponse<String> uploadTag(@RequestBody TagRequest request) {
        return articleService.addTag(request);
    }
} 