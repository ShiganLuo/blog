package com.baofeng.blog.enums;

import java.util.Map;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.Set;

public enum GenderEnum {
    MAN("男",1),WOMAN("女",2),UNKONWN("未知",9);
    private final String name;
    private final Integer code;
    GenderEnum(String name, int code) {
        this.name = name;
        this.code = code;
    }
    public String getName() {
        return name;
    }
    public Integer getCode() {
        return code;
    }
    private static final Map<Integer, GenderEnum> CODE_MAP = 
        Arrays.stream(GenderEnum.values())
              .collect(Collectors.toMap(GenderEnum::getCode, Function.identity()));
    
    private static final Map<String, GenderEnum> NAME_MAP = 
        Arrays.stream(GenderEnum.values())
              .collect(Collectors.toMap(GenderEnum::getName, Function.identity()));
    
    private static final Set<Integer> VALID_CODES = 
        Arrays.stream(GenderEnum.values())
            .map(GenderEnum::getCode)
            .collect(Collectors.toSet());
    
    public static GenderEnum fromCode(Integer code) {
        // 使用缓存的 Map 进行查找，如果找不到，默认返回 UNKNOWN
        return CODE_MAP.getOrDefault(code, UNKONWN);
    }

    public static GenderEnum fromName(String name) {
        // 使用缓存的 Map 进行查找，如果找不到，默认返回 UNKNOWN
        return NAME_MAP.getOrDefault(name, UNKONWN);
    }
    
    public static boolean isCodeExit(Integer code) {
        return VALID_CODES.contains(code);
    }

}
