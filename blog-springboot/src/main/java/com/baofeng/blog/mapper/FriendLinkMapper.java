package com.baofeng.blog.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;

import com.baofeng.blog.dto.admin.AdminFriendLinkDTO.AdminFriendLinkItem;
import com.baofeng.blog.dto.front.FrontFriendLinkDTO.FrontAddFriendLinkRequest;
import com.baofeng.blog.dto.front.FrontFriendLinkDTO.FrontFriendLinkItem;
import com.baofeng.blog.entity.FriendLink;

@Mapper
public interface FriendLinkMapper {

    /**
     * 获取所有友链信息
     * @return
     */
    List<FrontFriendLinkItem> getAllFriendLinksFront();

    /**
     * 增加友链
     * @param addFriendLinkRequest
     * @return
     */
    int insertFriendLink(FrontAddFriendLinkRequest frontAddFriendLinkRequest);

    /**
     * 更新友链信息
     * @param addFriendLinkRequest
     * @return
     */
    int updateFriendLinkById(FrontAddFriendLinkRequest frontAddFriendLinkRequest);

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
    int deleteFriendLinksByIds(List<Long> ids);

    /**
     * 获取后台友链列表
     * @param kyeword
     * @return
     */
    List<AdminFriendLinkItem> getAllFriendLinksForAdmin(String kyeword);
}
