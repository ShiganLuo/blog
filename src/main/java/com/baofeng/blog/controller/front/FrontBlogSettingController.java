package com.baofeng.blog.controller.front;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.baofeng.blog.service.BlogSettingService;
import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.front.FrontBlogSettinVO.updateSettingRequest;
import com.baofeng.blog.entity.BlogSetting;

import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/api/front/settings")
@RequiredArgsConstructor
@Validated
public class FrontBlogSettingController {
    private final BlogSettingService blogSettingService;

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
    public ApiResponse<String> changeSetting(@RequestBody updateSettingRequest request){
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
