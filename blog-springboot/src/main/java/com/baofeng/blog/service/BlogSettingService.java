package com.baofeng.blog.service;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.admin.AdminBlogSettingDTO.AdminConfigDetailResponse;
import com.baofeng.blog.dto.admin.AdminBlogSettingDTO.SystemSettingDictResponse;
import com.baofeng.blog.dto.front.FrontBlogSettingDTO.FrontBackgroundResponse;
import com.baofeng.blog.dto.front.FrontBlogSettingDTO.FrontConfigDetailResponse;
import com.baofeng.blog.dto.front.FrontBlogSettingDTO.SomeFrontInformation;
import com.baofeng.blog.entity.BlogSetting;

public interface BlogSettingService {

    /**
     * 增加网站访问量
     * @return
     */
    public ApiResponse<String> addViews();

    /**
     * 初始化网站设置
     * @param blogSetting
     * @return
     */
    public ApiResponse<String> initSetting(BlogSetting blogSetting);

    /**
     * 更新网站设置
     * @param blogSetting
     * @return
     */
    public ApiResponse<String> updateSetting(BlogSetting blogSetting);

    /**
     * 获取网站设置前台展示（根据设置id）
     * @param id 设置id
     * @return
     */
    public ApiResponse<FrontConfigDetailResponse> getSettingByIdFront(Long id);

    /**
     * 获取当前登录用户的博客设置（管理后台）
     * @return
     */
    public ApiResponse<AdminConfigDetailResponse> getSettingByCurrentUserAdmin();

    /**
     * 获取网站设置后台展示（根据用户id）
     * @param userId 用户id
     * @return
     */
    public ApiResponse<AdminConfigDetailResponse> getSettingByIdAdmin(Long userId);
    
    /**
     * 获取系统字典配置
     * @param type
     * @return
     */
    public ApiResponse<SystemSettingDictResponse> getSystemSettingDict(String type);

    /**
     * 获取ICP备案号（当前用户）
     * @return
     */
    public ApiResponse<SomeFrontInformation> getSomeFrontInformation();

    /**
     * 获取指定用户的ICP备案号等信息
     * @param userId 用户id
     * @return
     */
    public ApiResponse<SomeFrontInformation> getSomeFrontInformationById(Long userId);
    
    /**
     * 获取前台背景图片（当前用户）
     * @return
     */
    public ApiResponse<FrontBackgroundResponse> getFrontBackgroud();

    /**
     * 获取指定用户的前台背景图片
     * @param userId 用户id
     * @return
     */
    public ApiResponse<FrontBackgroundResponse> getFrontBackgroudById(Long userId);

}
