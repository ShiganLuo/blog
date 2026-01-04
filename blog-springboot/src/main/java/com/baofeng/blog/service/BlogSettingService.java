package com.baofeng.blog.service;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.admin.AdminBlogSettingDTO.AdminConfigDetailResponse;
import com.baofeng.blog.dto.admin.AdminBlogSettingDTO.SystemSettingDictResponse;
import com.baofeng.blog.dto.front.FrontBlogSettinDTO.FrontConfigDetailResponse;
import com.baofeng.blog.dto.front.FrontBlogSettinDTO.SomeFrontInformation;
import com.baofeng.blog.entity.BlogSetting;

public interface BlogSettingService {

    /**
     * 增加网站访问量
     * @return
     */
    public ApiResponse<String> addViews();

    /**
     * 初始化网站设置
     * @param request
     * @return
     */
    public ApiResponse<String> initSetting(BlogSetting blogSetting);

    /**
     * 更新网站设置
     * @param request
     * @return
     */
    public ApiResponse<String> updateSetting(BlogSetting blogSetting);

    /**
     * 获取网站设置前台展示
     * @param id
     * @return
     */
    public ApiResponse<FrontConfigDetailResponse> getSettingByIdFront(Long id);

    /**
     * 获取网站设置后台展示
     * @param id
     * @return
     */
    public ApiResponse<AdminConfigDetailResponse> getSettingByIdAdmin(Long id);

    
    /**
     * 获取系统字典配置
     * @param type
     * @return
     */
    public ApiResponse<SystemSettingDictResponse> getSystemSettingDict(String type);

    /**
     * 获取ICP备案号
     * @return
     */
    public ApiResponse<SomeFrontInformation> getSomeFrontInformation();
    

} 