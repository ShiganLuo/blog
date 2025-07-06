package com.baofeng.blog.mapper;

import com.baofeng.blog.vo.front.FrontTagVO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface FrontTagMapper {
    /**
     * 获取所有标签（包含文章数量）
     * @return 标签列表
     */
    List<FrontTagVO> getAllTags();

    /**
     * 获取热门标签（按文章数量排序）
     * @param limit 限制数量
     * @return 标签列表
     */
    List<FrontTagVO> getHotTags(int limit);

    /**
     * 获取标签详细信息
     * @param id 标签ID
     * @return 标签详细信息
     */
    FrontTagVO.TagDetailVO getTagDetail(Long id);
} 