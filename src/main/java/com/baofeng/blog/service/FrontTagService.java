package com.baofeng.blog.service;

import com.baofeng.blog.vo.front.FrontTagVO;
import java.util.List;

public interface FrontTagService {
    /**
     * 获取所有标签
     * @return 标签列表
     */
    List<FrontTagVO> getAllTags();

    /**
     * 获取热门标签
     * @param limit 限制数量
     * @return 标签列表
     */
    List<FrontTagVO> getHotTags(int limit);

    /**
     * 获取标签详情
     * @param id 标签ID
     * @return 标签详细信息
     */
    FrontTagVO.TagDetailVO getTagDetail(Long id);
} 