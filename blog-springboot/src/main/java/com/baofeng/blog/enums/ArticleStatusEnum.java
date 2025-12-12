package com.baofeng.blog.enums;

public enum ArticleStatusEnum {
    
    PUBLIC("公开",1),
    SECRET("私密",2),
    DRAFT("草稿",3);
    
    private final String status;
    private final Integer code;

    ArticleStatusEnum(String status,Integer code) {
        this.status = status;
        this.code = code;
    }
    public String getStatus() {
        return status;
    }
    public Integer getCode() {
        return code;
    }

    
    
    
}
