package com.baofeng.blog.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

import com.baofeng.blog.entity.BlogSetting;

@Mapper
public interface BlogSettingMapper {
    /**
     * 获取所有博客设置
     * @return 设置列表
     */
    List<BlogSetting> getAllSettings();

    /**
     * 根据id获取博客设置
     * @param id 网站id
     * @return 设置信息
     */
    BlogSetting getSettingById(Long id);

    /**
     * 根据用户id获取博客设置
     * @param userId 用户id
     * @return 设置信息
     */
    BlogSetting getSettingByUserId(Long userId);

    /**
     * 选择性更新设置
     * @param setting 设置信息
     * @return 影响的行数
     */
    int updateSettingById(BlogSetting setting);

    /**
     * 全量更新设置（允许将字段置为null）
     * @param setting 设置信息
     * @return 影响的行数
     */
    int updateSettingFull(BlogSetting setting);

    /**
     * 增加网站访问次数
     * @param count 增加的数量
     * @param id 设置id
     * @return 影响的行数
     */
    int incrementVisitCountById(@Param("count") int count, @Param("id") long id);

    /**
     * 初始化网站信息
     * @param setting 设置信息
     * @return 影响的行数
     */
    int insertSetting(BlogSetting setting);

    /**
     * 获取ICP备案号
     * @param id 设置id
     * @return ICP备案号
     */
    String getICPFilingNumberById(Long id);

    /**
     * 根据用户id删除设置
     * @param userId 用户id
     * @return 影响的行数
     */
    int deleteSettingByUserId(Long userId);
}
