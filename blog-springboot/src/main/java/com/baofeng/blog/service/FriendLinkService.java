package com.baofeng.blog.service;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.front.FrontBlogSettinDTO.AddFriendLinkRequest;
import com.baofeng.blog.dto.front.FrontBlogSettinDTO.FriendLinkPackResponse;
import com.baofeng.blog.dto.front.FrontBlogSettinDTO.FriendLinkRequest;


public interface FriendLinkService {
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
