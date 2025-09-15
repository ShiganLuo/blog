package com.baofeng.blog.controller.admin;

import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.admin.AdminRouteVO;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

@RestController
@RequestMapping("/api/admin/routes")
@RequiredArgsConstructor
public class AdminRouteController {

    @GetMapping("/get-async-routes")
    public ApiResponse<List<AdminRouteVO.routeResponse>> getAsyncRoutes() {
        List<AdminRouteVO.routeResponse> routeResponses = new ArrayList<>();
    
        // 构造父级路由
        AdminRouteVO.routeResponse response = new AdminRouteVO.routeResponse();
        AdminRouteVO.parent parentRoute = new AdminRouteVO.parent();
        parentRoute.setPath("/user");
        parentRoute.setName("UserManagement");
        
        AdminRouteVO.parentMeta parentMeta = new AdminRouteVO.parentMeta();
        parentMeta.setTitle("用户管理");
        parentMeta.setIcon("ep:user");
        parentMeta.setRank(10);
        parentMeta.setShowlink(true);
        parentRoute.setMeta(parentMeta);
        
        // 构造子路由
        AdminRouteVO.child child1 = new AdminRouteVO.child();
        child1.setPath("/user/index");
        child1.setName("PermissionPage");
        AdminRouteVO.childMeta childMeta1 = new AdminRouteVO.childMeta();
        childMeta1.setTitle("用户列表");
        childMeta1.setRoles(Arrays.asList("admin", "common"));
        child1.setMeta(childMeta1);
        
        AdminRouteVO.child child2 = new AdminRouteVO.child();
        child2.setPath("/permission/button");
        AdminRouteVO.childMeta childMeta2 = new AdminRouteVO.childMeta();
        childMeta2.setTitle("按钮权限");
        childMeta2.setRoles(Arrays.asList("admin", "common"));
        child2.setMeta(childMeta2);
        
        // 构造子路由的子路由
        AdminRouteVO.child buttonRouter = new AdminRouteVO.child();
        buttonRouter.setPath("/permission/button/router");
        buttonRouter.setComponent("permission/button/index");
        buttonRouter.setName("PermissionButtonRouter");
        AdminRouteVO.childMeta buttonRouterMeta = new AdminRouteVO.childMeta();
        buttonRouterMeta.setTitle("路由返回按钮权限");
        buttonRouterMeta.setAuths(Arrays.asList("permission:btn:add", "permission:btn:edit", "permission:btn:delete"));
        buttonRouter.setMeta(buttonRouterMeta);
        
        AdminRouteVO.child buttonLogin = new AdminRouteVO.child();
        buttonLogin.setPath("/permission/button/login");
        buttonLogin.setComponent("permission/button/perms");
        buttonLogin.setName("PermissionButtonLogin");
        AdminRouteVO.childMeta buttonLoginMeta = new AdminRouteVO.childMeta();
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
    
}
