package com.baofeng.blog.controller.admin;

import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.admin.AdminMockVO;
import com.baofeng.blog.vo.admin.AdminMockVO.loginFunctionEnabledResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

@RestController
@RequestMapping("/api/admin/mock")
@RequiredArgsConstructor
public class AdminMockController {

    @GetMapping("/get-async-routes")
    public ApiResponse<List<AdminMockVO.routeResponse>> getAsyncRoutes() {
        List<AdminMockVO.routeResponse> routeResponses = new ArrayList<>();
    
        // 构造父级路由
        AdminMockVO.routeResponse response = new AdminMockVO.routeResponse();
        AdminMockVO.parent parentRoute = new AdminMockVO.parent();
        parentRoute.setPath("/user");
        parentRoute.setName("UserManagement");
        
        AdminMockVO.parentMeta parentMeta = new AdminMockVO.parentMeta();
        parentMeta.setTitle("用户管理");
        parentMeta.setIcon("ep:user");
        parentMeta.setRank(10);
        parentMeta.setShowlink(true);
        parentRoute.setMeta(parentMeta);
        
        // 构造子路由
        AdminMockVO.child child1 = new AdminMockVO.child();
        child1.setPath("/user/index");
        child1.setName("PermissionPage");
        AdminMockVO.childMeta childMeta1 = new AdminMockVO.childMeta();
        childMeta1.setTitle("用户列表");
        childMeta1.setRoles(Arrays.asList("admin", "common"));
        child1.setMeta(childMeta1);
        
        AdminMockVO.child child2 = new AdminMockVO.child();
        child2.setPath("/permission/button");
        AdminMockVO.childMeta childMeta2 = new AdminMockVO.childMeta();
        childMeta2.setTitle("按钮权限");
        childMeta2.setRoles(Arrays.asList("admin", "common"));
        child2.setMeta(childMeta2);
        
        // 构造子路由的子路由
        AdminMockVO.child buttonRouter = new AdminMockVO.child();
        buttonRouter.setPath("/permission/button/router");
        buttonRouter.setComponent("permission/button/index");
        buttonRouter.setName("PermissionButtonRouter");
        AdminMockVO.childMeta buttonRouterMeta = new AdminMockVO.childMeta();
        buttonRouterMeta.setTitle("路由返回按钮权限");
        buttonRouterMeta.setAuths(Arrays.asList("permission:btn:add", "permission:btn:edit", "permission:btn:delete"));
        buttonRouter.setMeta(buttonRouterMeta);
        
        AdminMockVO.child buttonLogin = new AdminMockVO.child();
        buttonLogin.setPath("/permission/button/login");
        buttonLogin.setComponent("permission/button/perms");
        buttonLogin.setName("PermissionButtonLogin");
        AdminMockVO.childMeta buttonLoginMeta = new AdminMockVO.childMeta();
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
