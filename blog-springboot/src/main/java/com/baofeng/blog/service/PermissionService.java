package com.baofeng.blog.service;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.admin.AdminPermissionDTO.*;

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
