package com.baofeng.blog.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baofeng.blog.entity.EntityImage;
import com.baofeng.blog.dto.admin.AdminEntityImageDTO.UpdateImageIdEntity;

@Mapper
public interface EntityImageMapper {
    /**
     * 创建article_images映射表记录
     * @param EntityImage 
     * @return 影响行数量
     */
    int insertEntityImage(EntityImage articleImage);

    /**
     * 更新图片id
     * @param updateImageIdEntity
     * @return
     */
    int updateImageId(UpdateImageIdEntity updateImageIdEntity);

    /**
     * 更新图片id，记录不存在则新建记录
     * @param updateImageIdEntity
     * @return
     */
    int upsertEntityImage(UpdateImageIdEntity updateImageIdEntity);
}
