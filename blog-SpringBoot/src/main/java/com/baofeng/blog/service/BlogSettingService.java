package com.baofeng.blog.service;
import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.admin.AdminBlogSettingVO.initSettingRequest;
import com.baofeng.blog.vo.front.FrontBlogSettinVO.configDetail;
import com.baofeng.blog.vo.front.FrontBlogSettinVO.*;
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
    public ApiResponse<String> initSetting(initSettingRequest request);

    /**
     * 更新网站设置
     * @param request
     * @return
     */
    public ApiResponse<String> updateSettingById(initSettingRequest request);

    /**
     * 获取网站设置
     * @param id
     * @return
     */
    public ApiResponse<configDetail> getSettingById(Long id);

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
} 