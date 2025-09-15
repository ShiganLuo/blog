package com.baofeng.blog.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baofeng.blog.entity.EntityImage;

@Mapper
public interface EntityImageMapper {
    /**
     * 创建article_images映射表记录
     * @param EntityImage 
     * @return 影响行数量
     */
    int insertEntityImage(EntityImage articleImage);
}
