package com.baofeng.blog.controller.front;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.front.FrontBlogSettinDTO.AddFriendLinkRequest;
import com.baofeng.blog.dto.front.FrontBlogSettinDTO.FriendLinkPackResponse;
import com.baofeng.blog.dto.front.FrontBlogSettinDTO.FriendLinkRequest;
import com.baofeng.blog.dto.front.FrontBlogSettinDTO.FrontConfigDetail;
import com.baofeng.blog.service.BlogSettingService;




@RestController
@RequestMapping("/api/front/settings")
@RequiredArgsConstructor
@Validated
public class FrontBlogSettingController {
    private final BlogSettingService blogSettingService;

    @PutMapping("/addView")
    public ApiResponse<String> addView(){
        return blogSettingService.addViews();
    }


    @GetMapping("/getBlogConfig")
    public ApiResponse<FrontConfigDetail> getBlogConfig() {
        return blogSettingService.getSettingByIdFront((long) 1);
    }

}
