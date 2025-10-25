package com.baofeng.blog.enums;

public enum UserStatusEnum {
    ACTIVE("ACTIEVE","活跃"),INACTIVE("INACTIVE","不活跃"),BANNED("BANNED","禁止");
    private final String status;
    private final String description;
    UserStatusEnum(String status, String description) {
        this.status = status;
        this.description = description;
    }
    public String getStatus() {
        return status;
    }
    public String getDescription() {
        return description;
    }
}
