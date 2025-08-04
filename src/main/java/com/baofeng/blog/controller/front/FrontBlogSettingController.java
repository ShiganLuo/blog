package com.baofeng.blog.controller.front;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import com.baofeng.blog.service.BlogSettingService;
import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.front.FrontBlogSettinVO.configDetail;




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
    public ApiResponse<configDetail> getBlogConfig() {
        return blogSettingService.getSettingById((long) 1);
    }
}
