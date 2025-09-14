package com.baofeng.blog.vo.front;

import jakarta.validation.constraints.NotNull;
// import lombok.Data;

public class FrontLikeVO {

    /**
     * 文章点赞
     */
    public record LikeRequest(
        @NotNull(message = "目标Id不能为空")
        Long for_id,
        @NotNull(message = "点赞类型不能为空")
        String type,
        Long user_id
    ) {}
    
}
