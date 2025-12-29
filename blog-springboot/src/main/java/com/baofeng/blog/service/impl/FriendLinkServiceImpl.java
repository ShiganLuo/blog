package com.baofeng.blog.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.admin.AdminFriendLinkDTO.AdminFriendLinkItem;
import com.baofeng.blog.dto.admin.AdminFriendLinkDTO.AdminFriendLinkPageResponse;
import com.baofeng.blog.dto.admin.AdminFriendLinkDTO.AdminFriendLinkRequest;
import com.baofeng.blog.dto.front.FrontFriendLinkDTO.FrontFriendLinkRequest;
import com.baofeng.blog.dto.front.FrontFriendLinkDTO.FrontFriendLinkItem;
import com.baofeng.blog.dto.front.FrontFriendLinkDTO.FrontFriendLinkPageResponse;
import com.baofeng.blog.dto.common.FriendLinkDTO.AddFriendLinkRequest;
import com.baofeng.blog.entity.FriendLink;
import com.baofeng.blog.entity.User;
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
        adminFriendLinkPageResponse.setList(pageInfo.getList());
        adminFriendLinkPageResponse.setTotal(pageInfo.getTotal());
        return ApiResponse.success(adminFriendLinkPageResponse);
    }

    @Override
    public ApiResponse<String> addOrUpdateFriendLink(AddFriendLinkRequest addFriendLinkRequest) {
        Long userId = addFriendLinkRequest.getUserId();
        if (userId == null) {
            return ApiResponse.error(ResultCodeEnum.UNAUTHORIZED, "请登录后申请友链");
        }
        int rowUpdated = friendLinkMapper.insertOrUpdateFriendLink(addFriendLinkRequest);
        return rowUpdated > 0 
            ? ApiResponse.success("友链添加成功")
            : ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR,"友链添加失败");
    }
    
}
