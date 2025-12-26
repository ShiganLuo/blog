package com.baofeng.blog.controller.admin;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.admin.AdminBlogSettingDTO;
import com.baofeng.blog.dto.admin.AdminBlogSettingDTO.SystemSettingDictResponse;
import com.baofeng.blog.dto.front.FrontBlogSettinDTO.ConfigDetail;
import com.baofeng.blog.service.BlogSettingService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;


@RestController
@RequestMapping("/api/admin/settings")
@RequiredArgsConstructor
@Validated
public class AdminBlogSettingController {
    
    private final BlogSettingService blogSettingService;
    @PostMapping("/initSetting")
    public ApiResponse<String> initSetting(@RequestBody AdminBlogSettingDTO.InitSettingRequest request){
        return blogSettingService.initSetting(request);

    }
    @PutMapping("/addView")
    public ApiResponse<String> addView(){
        return blogSettingService.addViews();
    }

    @PostMapping("/updateWebsiteInfo")
    public ApiResponse<String> updateWebsiteInfo(@RequestBody AdminBlogSettingDTO.InitSettingRequest request){
        return blogSettingService.updateSettingById(request);
    }

    @GetMapping("/getBlogConfig")
    public ApiResponse<ConfigDetail> getBlogConfig() {
        return blogSettingService.getSettingById((long) 1);
    }

    @GetMapping("/getDictSetting/{type}")
    public ApiResponse<SystemSettingDictResponse> getDictSetting(@PathVariable String type) {
        return blogSettingService.getSystemSettingDict(type);
    }
    

} 