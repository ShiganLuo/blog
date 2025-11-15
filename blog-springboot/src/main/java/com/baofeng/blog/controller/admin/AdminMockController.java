package com.baofeng.blog.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.admin.AdminMockDTO;
import com.baofeng.blog.dto.admin.AdminMockDTO.loginFunctionEnabledResponse;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

@RestController
@RequestMapping("/api/admin/mock")
@RequiredArgsConstructor
public class AdminMockController {

    @GetMapping("/get-async-routes")
    public ApiResponse<List<AdminMockDTO.routeResponse>> getAsyncRoutes() {
        List<AdminMockDTO.routeResponse> routeResponses = new ArrayList<>();
    
        // 构造父级路由
        AdminMockDTO.routeResponse response = new AdminMockDTO.routeResponse();
        AdminMockDTO.parent parentRoute = new AdminMockDTO.parent();
        parentRoute.setPath("/user");
        parentRoute.setName("UserManagement");
        
        AdminMockDTO.parentMeta parentMeta = new AdminMockDTO.parentMeta();
        parentMeta.setTitle("用户管理");
        parentMeta.setIcon("ep:user");
        parentMeta.setRank(10);
        parentMeta.setShowlink(true);
        parentRoute.setMeta(parentMeta);
        
        // 构造子路由
        AdminMockDTO.child child1 = new AdminMockDTO.child();
        child1.setPath("/user/index");
        child1.setName("PermissionPage");
        AdminMockDTO.childMeta childMeta1 = new AdminMockDTO.childMeta();
        childMeta1.setTitle("用户列表");
        childMeta1.setRoles(Arrays.asList("admin", "common"));
        child1.setMeta(childMeta1);
        
        AdminMockDTO.child child2 = new AdminMockDTO.child();
        child2.setPath("/permission/button");
        AdminMockDTO.childMeta childMeta2 = new AdminMockDTO.childMeta();
        childMeta2.setTitle("按钮权限");
        childMeta2.setRoles(Arrays.asList("admin", "common"));
        child2.setMeta(childMeta2);
        
        // 构造子路由的子路由
        AdminMockDTO.child buttonRouter = new AdminMockDTO.child();
        buttonRouter.setPath("/permission/button/router");
        buttonRouter.setComponent("permission/button/index");
        buttonRouter.setName("PermissionButtonRouter");
        AdminMockDTO.childMeta buttonRouterMeta = new AdminMockDTO.childMeta();
        buttonRouterMeta.setTitle("路由返回按钮权限");
        buttonRouterMeta.setAuths(Arrays.asList("permission:btn:add", "permission:btn:edit", "permission:btn:delete"));
        buttonRouter.setMeta(buttonRouterMeta);
        
        AdminMockDTO.child buttonLogin = new AdminMockDTO.child();
        buttonLogin.setPath("/permission/button/login");
        buttonLogin.setComponent("permission/button/perms");
        buttonLogin.setName("PermissionButtonLogin");
        AdminMockDTO.childMeta buttonLoginMeta = new AdminMockDTO.childMeta();
        buttonLoginMeta.setTitle("登录接口返回按钮权限");
        buttonLogin.setMeta(buttonLoginMeta);
        
        // 设置按钮权限路由的子路由
        child2.setChildren(Arrays.asList(buttonRouter, buttonLogin));
        
        // 将子路由添加到父路由
        parentRoute.setChildren(Arrays.asList(child1, child2));
        
        // 将父路由包装到 routeResponse 中
        response.setPa(parentRoute);
        
        // 将routeResponse添加到列表
        routeResponses.add(response);
    
        // 返回包装了List的ApiResponse
        return ApiResponse.success(routeResponses);
    }

    @GetMapping("/loginFunctionEnabled")
    public ApiResponse<loginFunctionEnabledResponse> loginFunctionEnabled() {    
        loginFunctionEnabledResponse response = new loginFunctionEnabledResponse();
        response.setForgetPasswordEnabled(true);
        response.setRegisterUserEnabled(true);
        response.setSliderEnabled(true);
    
        return ApiResponse.success(response);
    }
    
}
