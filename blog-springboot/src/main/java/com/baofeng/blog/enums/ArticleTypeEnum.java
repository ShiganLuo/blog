package com.baofeng.blog.enums;

public enum ArticleTypeEnum {
    ORIGINAL("原创", 1), REPRINT("转载", 2), TRANSLATION("翻译", 3);
    private final String type;
    private final Integer code;
    ArticleTypeEnum(String type, Integer code) {
        this.type = type;
        this.code = code;
    }
    public String getType() {
        return type;
    }
    public Integer getCode() {
        return code;
    }
}
