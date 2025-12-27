package com.baofeng.blog.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.front.FrontBlogSettinDTO.AddFriendLinkRequest;
import com.baofeng.blog.dto.front.FrontBlogSettinDTO.FriendLinkPackResponse;
import com.baofeng.blog.dto.front.FrontBlogSettinDTO.FriendLinkRequest;
import com.baofeng.blog.dto.front.FrontBlogSettinDTO.FriendLinkResponse;
import com.baofeng.blog.entity.FriendLink;
import com.baofeng.blog.entity.User;
import com.baofeng.blog.enums.ResultCodeEnum;
import com.baofeng.blog.service.FriendLinkService;
import com.baofeng.blog.mapper.FriendLinkMapper;
import com.baofeng.blog.mapper.UserMapper;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

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
    public ApiResponse<FriendLinkPackResponse> getAllFriendLink(FriendLinkRequest request) {
        int current = request.current() != null ? request.current() : 1;
        int size = request.size() != null ? request.size() : 10;
        PageHelper.startPage(current, size);
        List<FriendLinkResponse> list = friendLinkMapper.getAllFriendLinksFront();
        PageInfo<FriendLinkResponse> pageInfo = new PageInfo<>(list);
        FriendLinkPackResponse response = new FriendLinkPackResponse();
        response.setTotal(pageInfo.getTotal());
        response.setList(pageInfo.getList());
        return ApiResponse.success(response);
    }

    @Override
    public ApiResponse<String> addFriendLink(AddFriendLinkRequest addFriendLinkRequest) {
        int rowUpdated = friendLinkMapper.insertFriendLink(addFriendLinkRequest);
        User user = userMapper.selectUserById(addFriendLinkRequest.user_id());
        if (user == null) {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST,"用户不存在, 用户必须登录才能添加友链");
        }
        return rowUpdated > 0 
            ? ApiResponse.success("友链添加成功")
            : ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR,"友链添加失败");

    }

    @Override
    public ApiResponse<String> updateFriendLink(AddFriendLinkRequest addFriendLinkRequest) {
        if (addFriendLinkRequest.id() == null) {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST,"更新友链, id不能为空");
        }
        if (addFriendLinkRequest.id() == 1) {
            return ApiResponse.error(ResultCodeEnum.UNAUTHORIZED,"没有权限更新该友链");
        }
        FriendLink friendLink = friendLinkMapper.getFriendLinkById(addFriendLinkRequest.id());
        if (friendLink == null) {
            return ApiResponse.error(ResultCodeEnum.NOT_FOUND,"该友链不存在");
        }
        int rowUpdated = friendLinkMapper.updateFriendLinkById(addFriendLinkRequest);
        return rowUpdated > 0
            ? ApiResponse.success("友链更新成功")
            : ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR,"友链更新失败");
    }

    @Override
    public ApiResponse<String> deleteFriendLink(Long id) {
        if (id == (long) 1) {
            return ApiResponse.error(ResultCodeEnum.UNAUTHORIZED,"没有权限删除该友链");
        }
        int rowUpdated = friendLinkMapper.deleteFriendLinkById(id);
        return rowUpdated > 0
            ? ApiResponse.success("友链删除成功")
            : ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR,"友链删除失败");
    }
    
}
