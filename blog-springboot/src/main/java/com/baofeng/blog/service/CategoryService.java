package com.baofeng.blog.service;

import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.common.Category.CategoryDictionaryResponse;
import com.baofeng.blog.vo.admin.AdminCategoryPageVO.CategoryPageRequestVO;
import com.baofeng.blog.vo.admin.AdminCategoryPageVO.CategoryPageResponseVO;
import com.baofeng.blog.vo.admin.AdminCategoryPageVO.CreateCategoryRequest;

import java.util.List;
public interface CategoryService {
    /**
     * 分页查询分类列表
     * @param request 分页查询参数
     * @return 分页结果
     */
    public ApiResponse<CategoryPageResponseVO> getCategoryPage(CategoryPageRequestVO request);

    /**
     * 创建分类
     * @param request 创建分类请求
     * @return 是否创建成功
     */
    public ApiResponse<String> createCategory(CreateCategoryRequest request);

    /**
     * 删除分类
     * @param id 分类ID
     * @return 是否删除成功
     * @throws RuntimeException 当分类不存在或分类下有文章时抛出
     */
    public ApiResponse<String> deleteCategory(Long id);

    /**
     * 获取目录字典列表
     * @return
     */
    public ApiResponse<List<CategoryDictionaryResponse>> getCategoryDictionary();

    /**
     * 计算分类总数
     * @return 分类总数
     */
    Long countAllCategories();
}
