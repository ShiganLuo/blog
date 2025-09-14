package com.baofeng.blog.controller.front;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import com.baofeng.blog.service.BlogSettingService;
import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.front.FrontBlogSettinVO.AddFriendLinkRequest;
import com.baofeng.blog.vo.front.FrontBlogSettinVO.FriendLinkPackResponse;
import com.baofeng.blog.vo.front.FrontBlogSettinVO.FriendLinkRequest;
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

    @PostMapping("/getAllFriendLink")
    public ApiResponse<FriendLinkPackResponse> getAllFriendLink(@RequestBody FriendLinkRequest request) {
        return blogSettingService.getAllFriendLink(request);
    }

    @PostMapping("/addFriendLink")
    public ApiResponse<String> addFriendLink(@RequestBody AddFriendLinkRequest addFriendLinkRequest) {
        return blogSettingService.addFriendLink(addFriendLinkRequest);
    }

    @PostMapping("/updateFriendLink")
    public ApiResponse<String> updateFriendLink(@RequestBody AddFriendLinkRequest addFriendLinkRequest) {
        return blogSettingService.updateFriendLink(addFriendLinkRequest);
    }

    @DeleteMapping("/delteFirendLink/{id}")
    public ApiResponse<String> delteFrinedLink(@PathVariable Long id) {
        return blogSettingService.deleteFriendLink(id);
    }
}
