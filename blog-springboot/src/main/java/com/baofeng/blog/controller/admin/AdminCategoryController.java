package com.baofeng.blog.controller.admin;

import com.baofeng.blog.service.CategoryService;
import com.baofeng.blog.service.ArticleService;
import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.common.Category.CategoryDictionaryResponse;
import com.baofeng.blog.vo.admin.AdminArticleVO.CategoryRequest;
import com.baofeng.blog.vo.admin.AdminCategoryPageVO.CategoryPageRequestVO;
import com.baofeng.blog.vo.admin.AdminCategoryPageVO.CategoryPageResponseVO;
import com.baofeng.blog.vo.admin.AdminCategoryPageVO.CreateCategoryRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import java.util.List;
@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
@Validated
public class AdminCategoryController {
    
    private final CategoryService categoryService;
    private final ArticleService articleService;
    /**
     * 创建分类
     * @param request 创建分类请求
     * @return 创建结果
     */
    @PostMapping("/create")
    public ApiResponse<String> createCategory(@Validated @RequestBody CreateCategoryRequest request) {
        return categoryService.createCategory(request);
    }

    /**
     * 分页查询分类列表
     * @param request 分页查询参数
     * @return 分页结果
     */
    @PostMapping("/list")
    public ApiResponse<CategoryPageResponseVO> getCategoryPage(@Validated @RequestBody CategoryPageRequestVO request) {
        return categoryService.getCategoryPage(request);
    }

    /**
     * 删除分类
     * @param id 分类ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteCategory(@PathVariable Long id) {
        return categoryService.deleteCategory(id);
    }
    /**
     * 返回目录字典
     */
    @GetMapping("/getCategoryDictionary")
    public ApiResponse<List<CategoryDictionaryResponse>> getCategoryDictionary(){
        return categoryService.getCategoryDictionary();
    }

    /**
     * 增加分类接口,如果表中没有则添加
     */
    @PostMapping("/uploadCategory")
    public ApiResponse<String> uploadCategory(@RequestBody CategoryRequest request){
        return articleService.addCategory(request);
    }
}
