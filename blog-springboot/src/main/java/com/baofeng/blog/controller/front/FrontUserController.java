package com.baofeng.blog.controller.front;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.common.UserDTO.LoginRequest;
import com.baofeng.blog.dto.common.UserDTO.UserInfoResponse;
import com.baofeng.blog.dto.front.FrontUserDTO.FrontLoginResponseVO;
import com.baofeng.blog.service.UserService;

import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/api/front/users")
@Validated
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

}
