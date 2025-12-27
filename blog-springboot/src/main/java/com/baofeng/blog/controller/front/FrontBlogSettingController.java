package com.baofeng.blog.controller.front;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.front.FrontBlogSettinDTO.FrontConfigDetail;
import com.baofeng.blog.service.BlogSettingService;

import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/api/front/settings")
@Validated
public class FrontBlogSettingController {
    private final BlogSettingService blogSettingService;

    public FrontBlogSettingController (
        BlogSettingService blogSettingService
    ) {
        this.blogSettingService = blogSettingService;
    }

    @PutMapping("/addView")
    public ApiResponse<String> addView(){
        return blogSettingService.addViews();
    }


    @GetMapping("/getBlogConfig")
    public ApiResponse<FrontConfigDetail> getBlogConfig() {
        return blogSettingService.getSettingByIdFront(1L);
    }

}
