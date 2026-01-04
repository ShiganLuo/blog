package com.baofeng.blog.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baofeng.blog.common.util.UrlNormalizeUtil;
import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.admin.AdminFriendLinkDTO.AdminFriendLinkItem;
import com.baofeng.blog.dto.admin.AdminFriendLinkDTO.AdminFriendLinkPageResponse;
import com.baofeng.blog.dto.admin.AdminFriendLinkDTO.AdminFriendLinkRequest;
import com.baofeng.blog.dto.admin.AdminFriendLinkDTO.UpdateFriendLinkIsVisibleRequest;
import com.baofeng.blog.dto.admin.AdminFriendLinkDTO.UpdateFriendLinkStatusRequest;
import com.baofeng.blog.dto.front.FrontFriendLinkDTO.FrontFriendLinkRequest;
import com.baofeng.blog.dto.front.FrontFriendLinkDTO.FrontFriendLinkItem;
import com.baofeng.blog.dto.front.FrontFriendLinkDTO.FrontFriendLinkPageResponse;
import com.baofeng.blog.dto.common.FriendLinkDTO.AddFriendLinkRequest;
import com.baofeng.blog.entity.FriendLink;
import com.baofeng.blog.entity.User;
import com.baofeng.blog.enums.FriendLinkStatusEnum;
import com.baofeng.blog.enums.ResultCodeEnum;
import com.baofeng.blog.service.FriendLinkService;
import com.baofeng.blog.mapper.FriendLinkMapper;
import com.baofeng.blog.mapper.UserMapper;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FriendLinkServiceImpl implements FriendLinkService {

    private final FriendLinkMapper friendLinkMapper;
    private final UserMapper userMapper;
    private static final Logger logger = LoggerFactory.getLogger(FriendLinkService.class);
    
    public FriendLinkServiceImpl (
        FriendLinkMapper friendLinkMapper,
        UserMapper userMapper
    ) {
        this.friendLinkMapper = friendLinkMapper;
        this.userMapper = userMapper;
    }

    @Override
    public ApiResponse<FrontFriendLinkPageResponse> getAllFriendLinkFront(FrontFriendLinkRequest request) {
        int current = request.current() != null ? request.current() : 1;
        int size = request.size() != null ? request.size() : 10;
        PageHelper.startPage(current, size);
        List<FrontFriendLinkItem> list = friendLinkMapper.getAllFriendLinksFront();
        PageInfo<FrontFriendLinkItem> pageInfo = new PageInfo<>(list);
        FrontFriendLinkPageResponse response = new FrontFriendLinkPageResponse();
        response.setTotal(pageInfo.getTotal());
        response.setList(pageInfo.getList());
        return ApiResponse.success(response);
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<String> deleteFriendLinks(List<Long> ids) {

        if (ids == null || ids.isEmpty()) {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST, "ids 不能为空");
        }

        int affectedRows = friendLinkMapper.deleteFriendLinksByIds(ids);

        if (affectedRows != ids.size()) {
            throw new RuntimeException("部分友链删除失败");
        }

        return ApiResponse.success("友链删除成功");
    }


    @Override
    public ApiResponse<AdminFriendLinkPageResponse> getAllFriendLinkAdmin(AdminFriendLinkRequest adminFriendLinkRequest) {
        int current = adminFriendLinkRequest.current() != null ? adminFriendLinkRequest.current() : 1;
        int size = adminFriendLinkRequest.size() != null ? adminFriendLinkRequest.size() : 10;
        PageHelper.startPage(current, size);
        List<AdminFriendLinkItem> list = friendLinkMapper.getAllFriendLinksForAdmin(adminFriendLinkRequest.keyword());
        PageInfo<AdminFriendLinkItem> pageInfo = new PageInfo<>(list);
        AdminFriendLinkPageResponse adminFriendLinkPageResponse = new AdminFriendLinkPageResponse();
        for (AdminFriendLinkItem item : list) {
            item.setStatusName(FriendLinkStatusEnum.fromCode(item.getStatus()).getName());
        }
        adminFriendLinkPageResponse.setList(pageInfo.getList());
        adminFriendLinkPageResponse.setTotal(pageInfo.getTotal());
        return ApiResponse.success(adminFriendLinkPageResponse);
    }

    @Override
    public ApiResponse<String> addOrUpdateFriendLink(AddFriendLinkRequest addFriendLinkRequest) {
        Long userId = addFriendLinkRequest.getUserId();
        addFriendLinkRequest.setSiteLogo(UrlNormalizeUtil.stripUrlPrefix(addFriendLinkRequest.getSiteLogo()));
        if (userId == null) {
            return ApiResponse.error(ResultCodeEnum.UNAUTHORIZED, "请登录后申请友链");
        }
        int rowUpdated = 0;
        Long friendLinkId = addFriendLinkRequest.getId();
        if (friendLinkId != null) {
            FriendLink friendLink = friendLinkMapper.getFriendLinkById(friendLinkId);
            if (friendLink != null) {
                friendLink.setApplyMessage(addFriendLinkRequest.getApplyMessage());
                friendLink.setIsVisible(addFriendLinkRequest.getIsVisible());
                friendLink.setSiteName(addFriendLinkRequest.getSiteName());
                friendLink.setSiteDesc(addFriendLinkRequest.getSiteDesc());
                friendLink.setSiteLogo(addFriendLinkRequest.getSiteLogo());
                friendLink.setSiteUrl(addFriendLinkRequest.getSiteUrl());
                friendLink.setUserId(addFriendLinkRequest.getUserId());
                friendLink.setStatus(addFriendLinkRequest.getStatus());
                rowUpdated = friendLinkMapper.updateFriendLinkById(friendLink);
            } else {
                logger.warn("友链不存在，但是前端上传了友链id");
                rowUpdated = friendLinkMapper.addFriendLink(addFriendLinkRequest);
            }
        } else {
            rowUpdated = friendLinkMapper.addFriendLink(addFriendLinkRequest);
        }
        
        return rowUpdated > 0 
            ? ApiResponse.success("友链添加成功")
            : ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR,"友链添加失败");
    }

    @Override
    public ApiResponse<String> updateFriendLinkStatus(UpdateFriendLinkStatusRequest updateFriendLinkStatusRequest) {
        Long id = updateFriendLinkStatusRequest.id();
        FriendLink friendLink = friendLinkMapper.getFriendLinkById(id);
        if (friendLink == null) {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST,"该友链不存在");
        }
        Integer status = updateFriendLinkStatusRequest.status();
        FriendLinkStatusEnum friendLinkStatusEnum = FriendLinkStatusEnum.fromCode(status);
        if (friendLinkStatusEnum == null) {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST,"status非法，必须为0或1或2");
        }

        FriendLink friendLink2 = new FriendLink();
        friendLink2.setId(id);
        friendLink2.setStatus(status);
        int rowUpdated = friendLinkMapper.updateFriendLinkById(friendLink2);
        return rowUpdated > 0
            ? ApiResponse.success("友链状态更新成功")
            : ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR);

    }
    
    @Override
    public ApiResponse<String> updateFriendLinkIsVisible(UpdateFriendLinkIsVisibleRequest updateFriendLinkIsVisibleRequest){
        Long id = updateFriendLinkIsVisibleRequest.id();
        Boolean isVisible = updateFriendLinkIsVisibleRequest.isVisible();
        logger.info("isVisible:{}",isVisible);
        FriendLink friendLink = friendLinkMapper.getFriendLinkById(id);
        if (friendLink == null) {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST,"该友链不存在");
        }
        if (isVisible == null) {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST,"isVisble不能为空");
        }
        FriendLink friendLink2 = new FriendLink();
        friendLink2.setId(id);
        friendLink2.setIsVisible(isVisible);
        int rowUpdated = friendLinkMapper.updateFriendLinkById(friendLink2);
        return rowUpdated > 0
            ? ApiResponse.success("友链可视状态更新成功")
            : ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR);

    }

}
