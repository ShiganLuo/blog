package com.baofeng.blog.enums;

public enum RoleType {
    ADMIN("ADMIN", "管理员，拥有系统所有权限"),
    USER("USER", "普通用户，拥有基本浏览和操作权限");

    private final String name;
    private final String description;

    RoleType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}