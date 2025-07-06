package com.baofeng.blog.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baofeng.blog.entity.BlogSetting;

import java.util.List;

@Mapper
public interface BlogSettingMapper {
    /**
     * 获取所有博客设置
     * @return 设置列表
     */
    List<BlogSetting> getAllSettings();

    /**
     * 根据键获取设置
     * @param id 网站id
     * @return 设置信息
     */
    BlogSetting getSettingById(long id);

    /**
     * 删除网站设置信息
     * @param id 网站id
     * @return 设置信息
     */
    int deleteSettingById(long id);
    
    /**
     * 选择性更新设置
     * @param setting 设置信息
     * @return 影响的行数
     */
    int updateSettingById(BlogSetting setting);
    /**
     * 增加网站访问次数
     * @param setting 设置信息
     * @return 影响的行数
     */
    int incrementVisitCount(long id);

    /**
     * 初始化网站信息
     * @param setting 设置信息
     * @return 影响的行数
     */
    int insertSetting(BlogSetting setting);


}