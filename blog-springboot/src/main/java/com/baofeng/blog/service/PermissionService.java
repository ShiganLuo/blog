package com.baofeng.blog.service;

import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.admin.AdminPermissionVO.*;

public interface PermissionService {

    public ApiResponse<String> AssignPermissionForRole(AssignPermissionRequest assignPermissionRequest);
}
