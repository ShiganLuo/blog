package com.baofeng.blog.dto.admin;


public class AdminPermissionDTO {

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
    public record AddNewPermissionRequest(
        String name,
        String permission,
        String type,
        Long parentId,
        String path
    ){
        public AddNewPermissionRequest {
            if (permission == null || permission.isEmpty()) {
                throw new IllegalArgumentException("permission不能为空");
            }
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("name不能为空");
            }
        }
    }
}
