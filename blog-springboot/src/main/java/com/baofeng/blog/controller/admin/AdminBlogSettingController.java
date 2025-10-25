package com.baofeng.blog.controller.admin;

import com.baofeng.blog.service.BlogSettingService;
import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.admin.AdminBlogSettingVO;
import com.baofeng.blog.vo.admin.AdminBlogSettingVO.SystemSettingDictResponse;
import com.baofeng.blog.vo.front.FrontBlogSettinVO.configDetail;

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
    public ApiResponse<String> initSetting(@RequestBody AdminBlogSettingVO.InitSettingRequest request){
        return blogSettingService.initSetting(request);

    }
    @PutMapping("/addView")
    public ApiResponse<String> addView(){
        return blogSettingService.addViews();
    }

    @PostMapping("/changeSetting")
    public ApiResponse<String> changeSetting(@RequestBody AdminBlogSettingVO.InitSettingRequest request){
        return blogSettingService.updateSettingById(request);
    }

    @GetMapping("/getBlogConfig")
    public ApiResponse<configDetail> getBlogConfig() {
        return blogSettingService.getSettingById((long) 1);
    }

    @GetMapping("/getDictSetting/{type}")
    public ApiResponse<SystemSettingDictResponse> getDictSetting(@PathVariable String type) {
        return blogSettingService.getSystemSettingDict(type);
    }
    

} 