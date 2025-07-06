package com.baofeng.blog.controller.admin;

import com.baofeng.blog.service.CategoryService;
import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.admin.AdminCategoryPageVO.CategoryDictionaryResponse;
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

    /**
     * 创建分类
     * @param request 创建分类请求
     * @return 创建结果
     */
    @PostMapping("/create")
    public ApiResponse<String> createCategory(@RequestBody CreateCategoryRequest request) {
        // 参数校验
        if (request == null) {
            return ApiResponse.error(400, "请求参数不能为空");
        }
        if (request.name() == null || request.name().trim().isEmpty()) {
            return ApiResponse.error(400, "分类名称不能为空");
        }
        
        try {
            boolean success = categoryService.createCategory(request);
            if (success) {
                return ApiResponse.success(null);
            } else {
                return ApiResponse.error(500, "创建分类失败");
            }
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "创建失败：" + e.getMessage());
        }
    }

    /**
     * 分页查询分类列表
     * @param request 分页查询参数
     * @return 分页结果
     */
    @PostMapping("/list")
    public ApiResponse<CategoryPageResponseVO> getCategoryPage(@RequestBody CategoryPageRequestVO request) {
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
        
        try {
            return ApiResponse.success(categoryService.getCategoryPage(request));
        } catch (Exception e) {
            return ApiResponse.error(500, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 删除分类
     * @param id 分类ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteCategory(@PathVariable Long id) {
        try {
            boolean success = categoryService.deleteCategory(id);
            if (success) {
                return ApiResponse.success(null);
            } else {
                return ApiResponse.error(500, "删除分类失败");
            }
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "删除失败：" + e.getMessage());
        }
    }
    /**
     * 返回目录字典
     */
    @GetMapping("/getCategoryDictionary")
    public ApiResponse<List<CategoryDictionaryResponse>> getCategoryDictionary(){
        try{
            List<CategoryDictionaryResponse> categoryDictionaryResponse = categoryService.getCategoryDictionary();
            return ApiResponse.success(categoryDictionaryResponse);

        } catch (Exception e){
            return ApiResponse.error(400, "获取失败"+e.getMessage());
        }
    }
}
