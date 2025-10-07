package com.baofeng.blog.enums;

import java.util.Map;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.function.Function;

public enum GenderEnum {
    MAN("男",1),WOMAN("女",2),UNKONWN("未知",9);
    private final String name;
    private final int code;
    GenderEnum(String name, int code) {
        this.name = name;
        this.code = code;
    }
    public String getName() {
        return name;
    }
    public int getCode() {
        return code;
    }
    private static final Map<Integer, GenderEnum> CODE_MAP = 
        Arrays.stream(GenderEnum.values())
              .collect(Collectors.toMap(GenderEnum::getCode, Function.identity()));
    public static GenderEnum fromCode(int code) {
        // 使用缓存的 Map 进行查找，如果找不到，默认返回 UNKNOWN
        return CODE_MAP.getOrDefault(code, UNKONWN);
    }

}
