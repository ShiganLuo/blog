package com.baofeng.blog.dto.admin;

import jakarta.validation.constraints.NotEmpty;
public class AdminPermissionDTO {

    public static record AssignPermissionRequest (
        @NotEmpty String roleName,
        @NotEmpty String permission
    ){
    }
    public static record AddNewPermissionRequest(
        String name,
        @NotEmpty String permission,
        @NotEmpty String type,
        Long parentId,
        String path
    ){
    }
}
