package com.baofeng.blog.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;

import com.baofeng.blog.dto.front.FrontBlogSettinDTO.AddFriendLinkRequest;
import com.baofeng.blog.dto.front.FrontBlogSettinDTO.FriendLinkResponse;
import com.baofeng.blog.entity.FriendLink;

@Mapper
public interface FriendLinkMapper {

    /**
     * 获取所有友链信息
     * @return
     */
    List<FriendLinkResponse> getAllFriendLinksFront();

    /**
     * 增加友链
     * @param addFriendLinkRequest
     * @return
     */
    int insertFriendLink(AddFriendLinkRequest addFriendLinkRequest);

    /**
     * 更新友链信息
     * @param addFriendLinkRequest
     * @return
     */
    int updateFriendLinkById(AddFriendLinkRequest addFriendLinkRequest);

    /**
     * 根据id获取友链
     * @param id
     * @return
     */
    FriendLink getFriendLinkById(Long id);

    /**
     * 根据id删除友链
     * @param id
     * @return
     */
    int deleteFriendLinkById(Long id);
}
