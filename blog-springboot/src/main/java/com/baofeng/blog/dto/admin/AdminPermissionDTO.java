package com.baofeng.blog.dto.admin;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

import com.baofeng.blog.entity.Role;
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

    public static record AuthRoleResponse(
        List<RoleType> roles,
        UserInfo user
    ){}

    @Data
    public static class RoleType{
        Long roleId;
        String roleName;
        String roleDesc;
        List<String> permissions;
        LocalDateTime createdAt;
        LocalDateTime updatedAt;
    }

    @Data
    public static class UserInfo{
        Long userId;
        String nickName;
        String userName;
        List<String> roles;
    }
}
