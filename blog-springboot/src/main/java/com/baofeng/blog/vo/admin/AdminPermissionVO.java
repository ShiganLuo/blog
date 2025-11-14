package com.baofeng.blog.vo.admin;

import lombok.Data;

public class AdminPermissionVO {

    public record AssignPermissionRequest (
        String roleName,
        String permission
    ){
        public AssignPermissionRequest {
            if (roleName == null || roleName.isEmpty()) {
                throw new IllegalArgumentException("roleName不能为空");
            }
            if (permission == null || permission.isEmpty()) {
                throw new IllegalArgumentException("permission不能为空");
            };
        }
    }
}
