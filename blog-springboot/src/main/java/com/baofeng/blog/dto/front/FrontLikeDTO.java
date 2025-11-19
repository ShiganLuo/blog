package com.baofeng.blog.dto.front;

import jakarta.validation.constraints.NotNull;
// import lombok.Data;

public class FrontLikeDTO {

    /**
     * 文章点赞
     */
    public static record LikeRequest(
        @NotNull(message = "目标Id不能为空")
        Long for_id,
        @NotNull(message = "点赞类型不能为空")
        String type,
        Long user_id
    ) {}
    
}
