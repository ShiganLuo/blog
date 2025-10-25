package com.baofeng.blog.enums;

public enum RoleTypeEnum {
    ADMIN("ADMIN", "管理"),
    USER("USER", "普通用户");

    private final String role;
    private final String description;

    RoleTypeEnum(String role, String description) {
        this.role = role;
        this.description = description;
    }

    public String getRole() {
        return role;
    }

    public String getDescription() {
        return description;
    }
}