package com.baofeng.blog.controller.admin;

import com.baofeng.blog.dto.admin.AdminFriendLinkDTO.AdminFriendLinkRequest;
import com.baofeng.blog.dto.admin.AdminFriendLinkDTO.UpdateFriendLinkIsVisibleRequest;
import com.baofeng.blog.dto.admin.AdminFriendLinkDTO.UpdateFriendLinkStatusRequest;
import com.baofeng.blog.dto.admin.AdminFriendLinkDTO.AdminFriendLinkPageResponse;
import com.baofeng.blog.dto.common.FriendLinkDTO.AddFriendLinkRequest;
import com.baofeng.blog.service.FriendLinkService;
import com.baofeng.blog.dto.ApiResponse;

import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import java.util.List;


@RestController
@RequestMapping("/api/admin/link")
public class AdminFriendLinkController {

    private FriendLinkService friendLinkService;

    public AdminFriendLinkController(
        FriendLinkService friendLinkService
    ) {
        this.friendLinkService = friendLinkService;
    }

    @GetMapping("/getAllFriendLink")
    public ApiResponse<AdminFriendLinkPageResponse> getAllFriendLink(@RequestBody @Validated AdminFriendLinkRequest adminFriendLinkRequest) {
        return friendLinkService.getAllFriendLinkAdmin(adminFriendLinkRequest);
    }

    @DeleteMapping("/del")
    public ApiResponse<String> deleteFriendLink(@RequestBody List<Long> ids) {
        return friendLinkService.deleteFriendLinks(ids);
    }
    
    @PostMapping("/addOrUpdateFriendLink")
    public ApiResponse<String> addOrUpdateFriendLink(@RequestBody @Validated AddFriendLinkRequest addFriendLink) {
        return friendLinkService.addOrUpdateFriendLink(addFriendLink);
    }

    @PostMapping("/updateFriendLinkStatus")
    public ApiResponse<String> updateFriendLinkStatus(@RequestBody @Validated UpdateFriendLinkStatusRequest updateFriendLinkStatusRequest) {
        return friendLinkService.updateFriendLinkStatus(updateFriendLinkStatusRequest);
    }

    @PostMapping("/updateFriendLinkIsVisible")
    public ApiResponse<String> updateFriendLinkIsVisible(@RequestBody @Validated UpdateFriendLinkIsVisibleRequest updateFriendLinkIsVisibleRequest) {
        return friendLinkService.updateFriendLinkIsVisible(updateFriendLinkIsVisibleRequest);
    }
}
