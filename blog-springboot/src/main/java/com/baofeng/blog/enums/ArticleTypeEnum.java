package com.baofeng.blog.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum ArticleTypeEnum {
    ORIGINAL("原创", 1), 
    REPRINT("转载", 2), 
    TRANSLATION("翻译", 3);
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
    private static final Map<Integer, ArticleTypeEnum> CODE_MAP = 
        Arrays.stream(ArticleTypeEnum.values())
              .collect(Collectors.toMap(ArticleTypeEnum::getCode, Function.identity()));
    public static ArticleTypeEnum fromCode(Integer code) {
        return CODE_MAP.get(code);
    }

}
