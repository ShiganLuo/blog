package com.baofeng.blog.controller.admin;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.admin.AdminBlogSettingDTO.AdminConfigDetailResponse;
import com.baofeng.blog.dto.admin.AdminBlogSettingDTO.SystemSettingDictResponse;
import com.baofeng.blog.entity.BlogSetting;
import com.baofeng.blog.service.BlogSettingService;

import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;


@RestController
@RequestMapping("/api/admin/settings")
public class AdminBlogSettingController {
    
    private final BlogSettingService blogSettingService;

    public AdminBlogSettingController (
        BlogSettingService blogSettingService
    ) {
        this.blogSettingService = blogSettingService;
    }
    @PostMapping("/initSetting")
    public ApiResponse<String> initSetting(@RequestBody @Validated BlogSetting blogSetting){
        return blogSettingService.initSetting(blogSetting);

    }
    @PutMapping("/addView")
    public ApiResponse<String> addView(){
        return blogSettingService.addViews();
    }

    @PostMapping("/updateWebsiteInfo")
    public ApiResponse<String> updateWebsiteInfo(@RequestBody @Validated BlogSetting blogSetting){
        return blogSettingService.updateSetting(blogSetting);
    }

    @GetMapping("/getBlogConfig")
    public ApiResponse<AdminConfigDetailResponse> getBlogConfig() {
        return blogSettingService.getSettingByIdAdmin((long) 1);
    }

    @GetMapping("/getDictSetting/{type}")
    public ApiResponse<SystemSettingDictResponse> getDictSetting(@PathVariable String type) {
        return blogSettingService.getSystemSettingDict(type);
    }
    

} 