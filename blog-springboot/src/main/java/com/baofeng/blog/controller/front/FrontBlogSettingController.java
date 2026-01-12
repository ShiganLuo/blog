package com.baofeng.blog.controller.front;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.front.FrontBlogSettinDTO.FrontConfigDetailResponse;
import com.baofeng.blog.dto.front.FrontBlogSettinDTO.SomeFrontInformation;
import com.baofeng.blog.service.BlogSettingService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/front/settings")
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
    public ApiResponse<FrontConfigDetailResponse> getBlogConfig() {
        return blogSettingService.getSettingByIdFront(1L);
    }

    @GetMapping("/getSomeFrontInformation")
    public ApiResponse<SomeFrontInformation> getSomeFrontInformation() {
        return blogSettingService.getSomeFrontInformation();
    }

}
