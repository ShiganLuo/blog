package com.baofeng.blog.service;

import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.admin.AdminPermissionVO.*;

public interface PermissionService {

    /**
     * 为角色分配权限
     * @param assignPermissionRequest
     * @return
     */
    public ApiResponse<String> assignPermissionForRole(AssignPermissionRequest assignPermissionRequest);

    /**
     * 增加新权限
     * @param permission
     * @return
     */
    public ApiResponse<String> addNewPermission(AddNewPermissionRequest addNewPermissionRequest);
}
