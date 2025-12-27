package com.baofeng.blog.controller.front;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.front.FrontBlogSettinDTO.AddFriendLinkRequest;
import com.baofeng.blog.dto.front.FrontBlogSettinDTO.FriendLinkPackResponse;
import com.baofeng.blog.dto.front.FrontBlogSettinDTO.FriendLinkRequest;
import com.baofeng.blog.service.FriendLinkService;

import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/api/front/link")
@RequiredArgsConstructor
@Validated
public class FrongLinkController {
    private final FriendLinkService friendLinkService;

    @PostMapping("/getAllFriendLink")
    public ApiResponse<FriendLinkPackResponse> getAllFriendLink(@RequestBody FriendLinkRequest request) {
        return friendLinkService.getAllFriendLink(request);
    }

    @PostMapping("/addFriendLink")
    public ApiResponse<String> addFriendLink(@RequestBody AddFriendLinkRequest addFriendLinkRequest) {
        return friendLinkService.addFriendLink(addFriendLinkRequest);
    }

    @PostMapping("/updateFriendLink")
    public ApiResponse<String> updateFriendLink(@RequestBody AddFriendLinkRequest addFriendLinkRequest) {
        return friendLinkService.updateFriendLink(addFriendLinkRequest);
    }

    @DeleteMapping("/delteFirendLink/{id}")
    public ApiResponse<String> delteFrinedLink(@PathVariable Long id) {
        return friendLinkService.deleteFriendLink(id);
    }
}
