package com.baofeng.blog.service.impl;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.baofeng.blog.service.LikeService;
import com.baofeng.blog.mapper.ArticleMapper;
import com.baofeng.blog.mapper.CommentMapper;
import com.baofeng.blog.mapper.LikeMapper;
import com.baofeng.blog.entity.Like;
import com.baofeng.blog.enums.ResultCodeEnum;
import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.front.FrontLikeVO.LikeRequest;

@Service
// 替代@Autowerid显示注入
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService{

    private final ArticleMapper articleMapper;
    private final CommentMapper commentMapper;
    private final LikeMapper likeMapper;

    @Override
    public ApiResponse<Boolean> getIsLikeByLikeRequest(LikeRequest request) {
        Long forId = request.for_id();
        String type = request.type();
        Long userId = request.user_id();
        Long id = likeMapper.selectIdByLikeRequestAndStatus(forId, type, userId,true);
        return id != null
            ? ApiResponse.success(true)
            : ApiResponse.success(false);
    }

    @Override
    public ApiResponse<String> deleteLikeByLikeRequest(LikeRequest request) {
        Long forId = request.for_id();
        String type = request.type();
        Long userId = request.user_id();
        int rowUpdated = 0;

        // 判断目标是否存在
        boolean isTargetExist = switch (type) {
            case "post" -> articleMapper.selectCountById(forId);
            case "comment","message" -> commentMapper.selectCountById(forId);
            default -> false;
        };
        if (!isTargetExist) {
            return ApiResponse.error(404, "目标不存在或类型错误");
        }

        if (userId == null) {
            // 游客取消点赞
            switch (type) {
                case "post":
                    rowUpdated = articleMapper.decreaseLikeById(forId);
                    break;
                case "comment","message":
                    rowUpdated = commentMapper.decreaseLikeById(forId);
                    break;
                default:
                    return ApiResponse.error(ResultCodeEnum.BAD_REQUEST,"type错误,必须为post、comment或message");
            }
        } else {
            // 用户取消点赞
            switch (type) {
                case "post":
                    rowUpdated = articleMapper.decreaseLikeById(forId);
                    break;
                case "comment","message" :
                    rowUpdated = commentMapper.decreaseLikeById(forId);
                    break;
                default:
                    return ApiResponse.error(ResultCodeEnum.BAD_REQUEST,"type错误,必须为post、comment或message");
            }
            rowUpdated += likeMapper.updateLikesByLikeRequestAndStatus(forId, type, userId, false);
            return rowUpdated > 1
                ? ApiResponse.success()
                : ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR);
        }

        return rowUpdated > 0
                ? ApiResponse.success()
                : ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR);
    }


    @Override 
    public ApiResponse<String> addLikeByLikeRequest(LikeRequest request) {
    Long forId = request.for_id();
    String type = request.type();
    Long userId = request.user_id();
    int rowUpdated = 0;

    // 判断目标是否存在
    boolean isTargetExist = switch (type) {
        case "post" -> articleMapper.selectCountById(forId);
        case "comment","message" -> commentMapper.selectCountById(forId);
        default -> false;
    };
    if (!isTargetExist) {
        return ApiResponse.error(404, "目标不存在或类型错误");
    }

    // 点赞逻辑
    if (userId == null) {
        // 游客点赞
        switch(type) {
            case "post":
                rowUpdated = articleMapper.incrementLikeById(forId);
                break;
            case "comment","message":
                rowUpdated = commentMapper.incrementLikeById(forId);
                break;
            default:
                return ApiResponse.error(ResultCodeEnum.BAD_REQUEST,"type错误,必须为post、comment或message");
        }
    } else {
        // 用户点赞
        switch(type) {
            case "post":
                rowUpdated = articleMapper.incrementLikeById(forId);
                break;
            case "comment","message":
                rowUpdated = commentMapper.incrementLikeById(forId);
                break;
            default:
                return ApiResponse.error(ResultCodeEnum.BAD_REQUEST,"type错误,必须为post、comment或message");
        }
        Long isExist = likeMapper.selectIdByLikeRequestAndStatus(forId, type, userId, false);
        if (isExist != null) {
            rowUpdated += likeMapper.updateLikesByLikeRequestAndStatus(forId, type, userId, true);
        } else {
            // 使用try catch 防止并发问题
            try {
                Like like = new Like();
                like.setUserId(userId);
                like.setTargetId(forId);
                like.setTargetType(type);
                like.setCreatedAt(LocalDateTime.now());
                like.setStatus(true);
                rowUpdated += likeMapper.insertLikeByLike(like);
            } catch (DuplicateKeyException e) {
                // 已存在就更新为 true（也可以先查）
                rowUpdated += likeMapper.updateLikesByLikeRequestAndStatus(forId, type, userId, true);
            }
        }
        
        return rowUpdated > 1
                ? ApiResponse.success()
                : ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR);
    }

    return rowUpdated > 0
            ? ApiResponse.success()
            : ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR);
}
    
}
