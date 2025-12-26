package com.baofeng.blog.service;
import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.admin.AdminBlogSettingDTO.InitSettingRequest;
import com.baofeng.blog.dto.admin.AdminBlogSettingDTO.SystemSettingDictResponse;
import com.baofeng.blog.dto.front.FrontBlogSettinDTO.*;
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
    public ApiResponse<String> initSetting(InitSettingRequest initSettingRequest);

    /**
     * 更新网站设置
     * @param request
     * @return
     */
    public ApiResponse<String> updateSettingById(InitSettingRequest initSettingRequest);

    /**
     * 获取网站设置
     * @param id
     * @return
     */
    public ApiResponse<ConfigDetail> getSettingById(Long id);

    /**
     * 获取所有友链
     * @param request
     * @return
     */
    public ApiResponse<FriendLinkPackResponse> getAllFriendLink(FriendLinkRequest request);

    /**
     * 增加友链
     * @param addFriendLinkRequest
     * @return
     */
    public ApiResponse<String> addFriendLink(AddFriendLinkRequest addFriendLinkRequest);

    /**
     * 更新友链信息
     * @param addFriendLinkRequest
     * @return
     */
    public ApiResponse<String> updateFriendLink(AddFriendLinkRequest addFriendLinkRequest);

    /**
     * 删除友链
     * @param id
     * @return
     */
    public ApiResponse<String> deleteFriendLink(Long id);

    /**
     * 获取系统字典配置
     * @param type
     * @return
     */
    public ApiResponse<SystemSettingDictResponse> getSystemSettingDict(String type);
    
} 