package com.baofeng.blog.service;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.admin.AdminFriendLinkDTO.AdminFriendLinkPageResponse;
import com.baofeng.blog.dto.admin.AdminFriendLinkDTO.AdminFriendLinkRequest;
import com.baofeng.blog.dto.common.FriendLinkDTO.AddFriendLinkRequest;
import com.baofeng.blog.dto.front.FrontFriendLinkDTO.FrontFriendLinkPageResponse;
import com.baofeng.blog.dto.front.FrontFriendLinkDTO.FrontFriendLinkRequest;
import com.baofeng.blog.dto.admin.AdminFriendLinkDTO.UpdateFriendLinkStatusRequest;
import com.baofeng.blog.dto.admin.AdminFriendLinkDTO.UpdateFriendLinkIsVisibleRequest;

import java.util.List;


public interface FriendLinkService {
    /**
     * 前台获取所有友链
     * @param request
     * @return
     */
    public ApiResponse<FrontFriendLinkPageResponse> getAllFriendLinkFront(FrontFriendLinkRequest request);



    /**
     * 更新或新增友链
     * @param addOrUpdateFriendLink
     * @return
     */
    public ApiResponse<String> addOrUpdateFriendLink(AddFriendLinkRequest addOrUpdateFriendLink);


    /**
     * 删除友链
     * @param id
     * @return
     */
    public ApiResponse<String> deleteFriendLinks(List<Long> ids);

    /**
     * 获取后台友链列表
     * @param adminFriendLinkRequest
     * @return
     */
    public ApiResponse<AdminFriendLinkPageResponse> getAllFriendLinkAdmin(AdminFriendLinkRequest adminFriendLinkRequest);

    /**
     * 更新友链状态
     * @param updateFriendLinkStatusRequest
     * @return
     */
    public ApiResponse<String> updateFriendLinkStatus(UpdateFriendLinkStatusRequest updateFriendLinkStatusRequest);

    /**
     * 决定友链是否可见
     * @param updateFriendLinkIsVisible
     * @return
     */
    public ApiResponse<String> updateFriendLinkIsVisible(UpdateFriendLinkIsVisibleRequest updateFriendLinkIsVisible);

}
