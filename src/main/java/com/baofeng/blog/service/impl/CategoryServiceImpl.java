package com.baofeng.blog.service.impl;

import com.baofeng.blog.vo.admin.AdminCategoryPageVO.CategoryDictionaryResponse;
import com.baofeng.blog.vo.admin.AdminCategoryPageVO.CategoryPageRequestVO;
import com.baofeng.blog.vo.admin.AdminCategoryPageVO.CategoryPageResponseVO;
import com.baofeng.blog.vo.admin.AdminCategoryPageVO.CategoryVO;
import com.baofeng.blog.vo.admin.AdminCategoryPageVO.CreateCategoryRequest;
import com.baofeng.blog.entity.Category;
import com.baofeng.blog.mapper.CategoryMapper;
import com.baofeng.blog.service.CategoryService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryPageResponseVO getCategoryPage(CategoryPageRequestVO request) {
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
        return response;
    }

    @Override
    public boolean createCategory(CreateCategoryRequest request) {
        // 检查分类名称是否已存在
        if (categoryMapper.getCategoryByName(request.name()) != null) {
            throw new RuntimeException("分类名称已存在");
        }

        // 创建分类
        Category category = new Category();
        category.setName(request.name());
        category.setDescription(request.description());

        // 保存分类
        return categoryMapper.createCategory(category) > 0;
    }

    @Override
    public boolean deleteCategory(Long id) {
        // 检查分类是否存在
        Category category = categoryMapper.getCategoryById(id);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }

        // 检查分类下是否有文章
        int articleCount = categoryMapper.getArticleCount(id);
        if (articleCount > 0) {
            throw new RuntimeException("该分类下还有" + articleCount + "篇文章，无法删除");
        }

        // 删除分类
        return categoryMapper.deleteCategory(id) > 0;
    }
    @Override
    public List<CategoryDictionaryResponse> getCategoryDictionary(){
        List<CategoryDictionaryResponse> categoryDictionaryList = categoryMapper.getAllCategories();
        return categoryDictionaryList;
    }
}
