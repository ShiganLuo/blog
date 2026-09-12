package com.baofeng.blog.controller.front;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.common.UserDTO.LoginRequest;
import com.baofeng.blog.dto.common.UserDTO.UserInfoResponse;
import com.baofeng.blog.dto.front.FrontUserDTO.FrontLoginResponseVO;
import com.baofeng.blog.dto.front.FrontUserDTO.FrontUpdateUserInfoRequest;
import com.baofeng.blog.dto.front.FrontUserDTO.FrontUpdatePasswordRequest;
import com.baofeng.blog.dto.front.FrontUserDTO.FrontUserStatsResponse;
import com.baofeng.blog.dto.front.FrontUserDTO.FrontUserActivityResponse;
import com.baofeng.blog.service.UserService;

import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/api/front/users")
public class FrontUserController {
    
    private final UserService userService;

    public FrontUserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ApiResponse<FrontLoginResponseVO> login(@RequestBody @Validated LoginRequest request) {
        return userService.loginUserFront(request);

    }
    @GetMapping("/getUserInfoById/{id}")
    public ApiResponse<UserInfoResponse> getUserInfoById(@PathVariable Long id){
        return userService.getUserInfoById(id);
    }

    @PostMapping("/updateUserInfo")
    public ApiResponse<String> updateUserInfo(@RequestBody @Validated FrontUpdateUserInfoRequest request) {
        return userService.updateUserInfoFront(request);
    }

    @GetMapping("/getUserStats/{userId}")
    public ApiResponse<FrontUserStatsResponse> getUserStats(@PathVariable Long userId) {
        return userService.getUserStats(userId);
    }

    @GetMapping("/getUserActivity/{userId}")
    public ApiResponse<FrontUserActivityResponse> getUserActivity(@PathVariable Long userId) {
        return userService.getUserActivity(userId);
    }

    @PostMapping("/updatePassword")
    public ApiResponse<String> updatePassword(@RequestBody @Validated FrontUpdatePasswordRequest request) {
        return userService.updatePasswordFront(request);
    }
}
