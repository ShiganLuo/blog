package com.baofeng.blog.service;

import com.baofeng.blog.vo.admin.AdminTagPageVO.TagPageRequestVO;
import com.baofeng.blog.vo.admin.AdminTagPageVO.TagPageResponseVO;

import java.util.List;

import com.baofeng.blog.vo.admin.AdminTagPageVO.CreateTagRequest;
import com.baofeng.blog.vo.admin.AdminTagPageVO.TagDictionaryResponse;

public interface TagService {
    /**
     * 分页查询标签列表
     * @param request 分页查询参数
     * @return 分页结果
     */
    TagPageResponseVO getTagPage(TagPageRequestVO request);

    /**
     * 创建标签
     * @param request 创建标签请求
     * @return 是否创建成功
     */
    boolean createTag(CreateTagRequest request);

    /**
     * 删除标签
     * @param id 标签ID
     * @return 是否删除成功
     * @throws RuntimeException 当标签不存在或标签下有文章时抛出
     */
    boolean deleteTag(Long id);

    /**
     * 获取标签字典
     * @return 标签字典
     */
    List<TagDictionaryResponse> getTagDictionary();
} 