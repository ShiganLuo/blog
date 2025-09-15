package com.baofeng.blog.service;
import com.baofeng.blog.vo.front.FrontLikeVO.LikeRequest;
import com.baofeng.blog.vo.ApiResponse;

public interface LikeService {

    /**
     * 判断是否点赞
     * @param LikeRequest
     * @return 
     */
    public ApiResponse<Boolean> getIsLikeByLikeRequest(LikeRequest request);

    /**
     * 取消点赞
     * @param LikeRequest
     * @return 
     */
    public ApiResponse<String> deleteLikeByLikeRequest(LikeRequest request);
    
    /**
     * 点赞
     * @param LikeRequest
     * @return 
     */

    public ApiResponse<String> addLikeByLikeRequest(LikeRequest request);
}

