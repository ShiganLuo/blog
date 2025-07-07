package com.baofeng.blog.controller.admin;

import com.baofeng.blog.service.BlogSettingService;
import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.admin.AdminBlogSettingVO;
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
    public ApiResponse<String> initSetting(@RequestBody AdminBlogSettingVO.initSettingRequest request){
        try {
            boolean success = blogSettingService.initSetting(request);
            if ( success ){
                return ApiResponse.success("网站初始化成功");
            } else {
                return ApiResponse.error(400, "网站初始化失败");
            }
        } catch(Exception e){
            return ApiResponse.error(400, "网站初始化失败" + e.getMessage());
        }

    }
    @PutMapping("/addView")
    public ApiResponse<String> addView(){
        try {
            boolean success = blogSettingService.addViews();
            if ( success ){
                return ApiResponse.success("访问量增加成功");
            } else {
                return ApiResponse.error(400, "访问量增加失败");
            }
        } catch (Exception e){
            return ApiResponse.error(400, "增加失败" + e.getMessage());
        }
    }

    @PostMapping("/changeSetting")
    public ApiResponse<String> changeSetting(@RequestBody AdminBlogSettingVO.initSettingRequest request){
        try {
            boolean success = blogSettingService.updateSettingById(request);
            if ( success ){
                return ApiResponse.success("网站设置更新成功");
            } else {
                return ApiResponse.error(400, "网站设置更新失败");
            }
        } catch(Exception e){
            return ApiResponse.error(400, "网站设置更新失败" + e.getMessage());
        }
    }

} 