package com.baofeng.blog.service;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.admin.AdminTagPageDTO.CreateTagRequest;
import com.baofeng.blog.dto.admin.AdminTagPageDTO.TagPageRequestVO;
import com.baofeng.blog.dto.admin.AdminTagPageDTO.TagPageResponseVO;
import com.baofeng.blog.dto.common.TagDTO.TagDictionaryResponse;
import com.baofeng.blog.entity.Tag;

import java.util.List;


public interface TagService {
    /**
     * 分页查询标签列表
     * @param request 分页查询参数
     * @return 分页结果
     */
    public ApiResponse<TagPageResponseVO> getTagPage(TagPageRequestVO request);

    /**
     * 创建标签
     * @param request 创建标签请求
     * @return 是否创建成功
     */
    public ApiResponse<String> createTag(CreateTagRequest request);

    /**
     * 删除标签
     * @param id 标签ID
     * @return 是否删除成功
     */
    public ApiResponse<String> deleteTag(Long id);

    /**
     * 获取标签字典
     * @return 标签字典
     */
    public ApiResponse<List<TagDictionaryResponse>> getTagDictionary(String keyword);

    /**
     * 计算标签总数
     * @return 标签总数
     */
    Long countAllTags();

    /**
     * 获取热门标签
     * @param limit 限制数量
     * @return 标签列表
     */
    public ApiResponse<List<String>> getHotTags(int limit);

    /**
     * 获取标签详情
     * @param id 标签ID
     * @return 标签详细信息
     */
    public ApiResponse<Tag> getTagDetail(Long id);
} 