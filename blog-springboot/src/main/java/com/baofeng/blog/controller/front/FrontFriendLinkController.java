package com.baofeng.blog.controller.front;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.common.FriendLinkDTO.AddFriendLinkRequest;
import com.baofeng.blog.dto.front.FrontFriendLinkDTO.FrontFriendLinkPageResponse;
import com.baofeng.blog.dto.front.FrontFriendLinkDTO.FrontFriendLinkRequest;
import com.baofeng.blog.service.FriendLinkService;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/front/link")
@Validated
public class FrontFriendLinkController {
    private final FriendLinkService friendLinkService;
    public FrontFriendLinkController (
        FriendLinkService friendLinkService
    ) {
        this.friendLinkService = friendLinkService;
    }

    @PostMapping("/getAllFriendLink")
    public ApiResponse<FrontFriendLinkPageResponse> getAllFriendLink(@RequestBody FrontFriendLinkRequest request) {
        return friendLinkService.getAllFriendLinkFront(request);
    }

    @PostMapping("/addFriendLink")
    public ApiResponse<String> addFriendLink(@RequestBody AddFriendLinkRequest addFriendLinkRequest) {
        return friendLinkService.addOrUpdateFriendLink(addFriendLinkRequest);
    }

    @PostMapping("/updateFriendLink")
    public ApiResponse<String> updateFriendLink(@RequestBody AddFriendLinkRequest addFriendLinkRequest) {
        return friendLinkService.addOrUpdateFriendLink(addFriendLinkRequest);
    }

    @DeleteMapping("/delteFirendLink/{id}")
    public ApiResponse<String> delteFrinedLink(@PathVariable List<Long> ids) {
        return friendLinkService.deleteFriendLinks(ids);
    }
}
