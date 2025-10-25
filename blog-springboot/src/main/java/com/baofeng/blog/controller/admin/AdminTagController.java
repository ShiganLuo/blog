package com.baofeng.blog.controller.admin;

import com.baofeng.blog.service.TagService;
import com.baofeng.blog.service.ArticleService;
import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.admin.AdminTagPageVO.TagPageRequestVO;
import com.baofeng.blog.vo.admin.AdminTagPageVO.TagPageResponseVO;
import com.baofeng.blog.vo.admin.AdminArticleVO.TagRequest;
import com.baofeng.blog.vo.admin.AdminTagPageVO.CreateTagRequest;
import com.baofeng.blog.vo.common.Tag.TagDictionaryResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/api/admin/tags")
@RequiredArgsConstructor
@Validated
public class AdminTagController {
    
    private final TagService tagService;
    private final ArticleService articleService;

    /**
     * 创建标签
     * @param request 创建标签请求
     * @return 创建结果
     */
    @PostMapping("/create")
    public ApiResponse<String> createTag(@Validated @RequestBody CreateTagRequest request) {        
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
    public ApiResponse<TagPageResponseVO> getTagPage(@RequestBody TagPageRequestVO request) {
        return tagService.getTagPage(request);
    }
    /**
     * 查询标签列表
     * @return {id,name}
     */
    @GetMapping("/getTagDictionary")
    public ApiResponse<List<TagDictionaryResponse>> getTagDictionary(){
        return tagService.getTagDictionary(); // 注意返回修改了，记得修改前端
    }
    /**
     * 增加标签接口,如果表中没有则添加
     */
    @PostMapping("/uploadTag")
    public ApiResponse<String> uploadTag(@RequestBody TagRequest request) {
        return articleService.addTag(request);
    }
} 