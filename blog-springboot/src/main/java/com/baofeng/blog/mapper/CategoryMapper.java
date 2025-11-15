package com.baofeng.blog.mapper;

import com.baofeng.blog.dto.admin.AdminCategoryPageDTO.CategoryPageRequestVO;
import com.baofeng.blog.dto.admin.AdminCategoryPageDTO.CategoryVO;
import com.baofeng.blog.dto.common.CategoryDTO.CategoryDictionaryResponse;
import com.baofeng.blog.entity.ArticleCategory;
import com.baofeng.blog.entity.Category;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface CategoryMapper {
    /**
     * 分页查询分类列表
     * @param request 分页查询参数
     * @return 分类列表
     */
    List<CategoryVO> getCategoryPage(CategoryPageRequestVO request);

    /**
     * 创建分类
     * @param category 分类信息
     * @return 影响的行数
     */
    int createCategory(Category category);

    /**
     * 根据名称查询分类
     * @param name 分类名称
     * @return 分类信息
     */
    Category getCategoryByName(String name);

    /**
     * 删除分类
     * @param id 分类ID
     * @return 影响的行数
     */
    int deleteCategory(Long id);

    /**
     * 获取分类下的文章数量
     * @param id 分类ID
     * @return 文章数量
     */
    int getArticleCount(Long id);

    /**
     * 根据ID获取分类
     * @param id 分类ID
     * @return 分类信息
     */
    Category getCategoryById(Long id);
    /**
     * 获取目录字典
     */
    List<CategoryDictionaryResponse> getAllCategories();
    /**
     * 插入article_categories映射表记录
     * @param ArticleCategory
     * @return 影响行数量
     */
    int insertCategoryReflect(ArticleCategory articleCategory);
    /**
     * 获取所有分类名
     * @return 所有分类名
     */
    List<String> getAllCategoryName();
    /**
     * 判断name是否在表中有记录
     * @param name
     * @return 0不存在/1已存在
     */
   boolean checkExactName(String name);

   /**
    * 计算分类总数
    */
  Long countAllCategories();

  /**
   * 查看指定时间已存在的分类数
   * @param createdAt
   * @return
   */
  long selectCategoryCountWhenSpecifiedTime(@Param("createdAt") LocalDateTime createdAt);
} 