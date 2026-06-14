package com.baofeng.blog.controller.front;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.front.FrontBlogSettingDTO.FrontBackgroundResponse;
import com.baofeng.blog.dto.front.FrontBlogSettingDTO.FrontConfigDetailResponse;
import com.baofeng.blog.dto.front.FrontBlogSettingDTO.SomeFrontInformation;
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

    /**
     * 获取指定用户的博客配置（前台展示）
     * @param userId 用户id
     */
    @GetMapping("/getBlogConfig/{userId}")
    public ApiResponse<FrontConfigDetailResponse> getBlogConfig(@PathVariable Long userId) {
        return blogSettingService.getSettingByIdFront(userId);
    }

    /**
     * 获取指定用户的部分前台信息
     * @param userId 用户id
     */
    @GetMapping("/getSomeFrontInformation/{userId}")
    public ApiResponse<SomeFrontInformation> getSomeFrontInformation(@PathVariable Long userId) {
        return blogSettingService.getSomeFrontInformationById(userId);
    }

    /**
     * 获取指定用户的前台背景图片
     * @param userId 用户id
     */
    @GetMapping("/getFrontBackground/{userId}")
    public ApiResponse<FrontBackgroundResponse> getFrontBackground(@PathVariable Long userId) {
        return blogSettingService.getFrontBackgroudById(userId);
    }
}
