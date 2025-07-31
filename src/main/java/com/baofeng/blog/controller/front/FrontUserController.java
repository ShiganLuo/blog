package com.baofeng.blog.controller.front;

import com.baofeng.blog.service.UserService;
import com.baofeng.blog.util.JwtTokenProvider;
import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.common.User.LoginRequest;
import com.baofeng.blog.vo.front.FrontUserVO.FrontLoginResponseVO;
import com.baofeng.blog.entity.User;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/api/front/users")
@Validated

public class FrontUserController {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public FrontUserController(UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public ApiResponse<FrontLoginResponseVO> login(@RequestBody @Validated LoginRequest request) {
        try {
            User user = userService.loginUser(request);
            if (user != null) {
                // 生成 token
                //  accessTokenExpiration: 3600000    # 1 小时（60 分钟 = 60 * 60 * 1000 ms）
                //   refreshTokenExpiration: 1209600000  # 14 天（14 * 24 * 60 * 60 * 1000 ms
                String accessToken = jwtTokenProvider.generateToken(user,3600000,false);
                String refreshToken = jwtTokenProvider.generateToken(user, 1209600000,true);
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime expires = now.plus(1, ChronoUnit.HOURS);
                // 构造 Token 和 User 信息（内部类）
                FrontLoginResponseVO.User userInfo = new FrontLoginResponseVO.User(
                        user.getId(),
                        user.getAvatarUrl(),
                        user.getUsername(),
                        user.getNickName(),
                        user.getRole().name()
                );
                // 封装并返回
                FrontLoginResponseVO response = new FrontLoginResponseVO(accessToken,refreshToken,expires,userInfo);
                return ApiResponse.success(response);
            } else {
                return ApiResponse.error(404, "此用户不存在");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ApiResponse.error(400, "登录失败");
        }

    }
    @GetMapping("/getUserInfoById/{id}")
    public ApiResponse<User> getUserInfoById(@PathVariable Long id){
        try {
            User user = userService.getUserInfoById(id);
            if (user != null){
                return ApiResponse.success(user);
            } else {
                return ApiResponse.error(401, "用户不存在");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ApiResponse.error(400, "获取用户信息失败");
        }
        

    }

}
