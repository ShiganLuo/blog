package com.baofeng.blog.service.impl;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.admin.AdminCategoryPageDTO.CategoryPageRequestVO;
import com.baofeng.blog.dto.admin.AdminCategoryPageDTO.CategoryPageResponseVO;
import com.baofeng.blog.dto.admin.AdminCategoryPageDTO.CategoryVO;
import com.baofeng.blog.dto.admin.AdminCategoryPageDTO.CreateCategoryRequest;
import com.baofeng.blog.dto.common.CategoryDTO.CategoryDictionaryResponse;
import com.baofeng.blog.entity.Category;
import com.baofeng.blog.enums.ResultCodeEnum;
import com.baofeng.blog.mapper.CategoryMapper;
import com.baofeng.blog.service.CategoryService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryMapper categoryMapper;
    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);
    @Override
    public ApiResponse<CategoryPageResponseVO> getCategoryPage(CategoryPageRequestVO request) {
        // 参数校验
        int pageNum = request.pageNum() != null ? request.pageNum() : 1;
        int pageSize = request.pageSize() != null ? request.pageSize() : 10;
        
        // 开启分页
        PageHelper.startPage(pageNum, pageSize);
        
        // 执行查询
        List<CategoryVO> list = categoryMapper.getCategoryPage(request);
        
        // 获取分页信息
        PageInfo<CategoryVO> pageInfo = new PageInfo<>(list);
        
        // 封装返回结果
        CategoryPageResponseVO response = new CategoryPageResponseVO();
        response.setTotal(pageInfo.getTotal());    // 总记录数
        response.setPages(pageInfo.getPages());    // 总页数
        response.setList(pageInfo.getList());      // 当前页数据
        return ApiResponse.success(response);
    }

    @Override
    public ApiResponse<String> createCategory(CreateCategoryRequest request) {
        // 检查分类名称是否已存在
        if (categoryMapper.getCategoryByName(request.name()) != null) {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST,"分类名称已存在");
        }

        // 创建分类
        Category category = new Category();
        category.setName(request.name());
        category.setDescription(request.description());
        int rowsUpdated = categoryMapper.createCategory(category);

        return rowsUpdated > 0
            ? ApiResponse.success()
            : ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR,"分类创建失败");
    }

    @Override
    public ApiResponse<String> deleteCategory(Long id) {
        // 检查分类是否存在
        Category category = categoryMapper.getCategoryById(id);
        if (category == null) {
            return ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR,"分类不存在");
        }

        // 检查分类下是否有文章
        int articleCount = categoryMapper.getArticleCount(id);
        if (articleCount > 0) {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST,"该分类下还有" + articleCount + "篇文章，无法删除");
        }
        int rowsUpdated = categoryMapper.deleteCategory(id);

        return rowsUpdated > 0 
         ? ApiResponse.success()
         : ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR,"删除失败");
    }
    @Override
    public ApiResponse<List<CategoryDictionaryResponse>> getCategoryDictionary(String keyword){
        List<CategoryDictionaryResponse> categoryDictionaryList = categoryMapper.getCategoriesByKeyword(keyword);
        return ApiResponse.success(categoryDictionaryList);
    }

    @Override
    public Long countAllCategories(){
        Long categoryCount = categoryMapper.countAllCategories();
        return categoryCount;
    }
}
