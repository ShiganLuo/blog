package com.baofeng.blog.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum FriendLinkStatusEnum {

    REVIEW("待审核",0),
    PASS("已通过",1),
    FORBIDDEN("已禁用",2);


    private String name;
    private Integer code;

    FriendLinkStatusEnum(String name,Integer code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public Integer getCode() {
        return code;
    }

    private static final Map<Integer, FriendLinkStatusEnum> CODE_MAP = 
        Arrays.stream(FriendLinkStatusEnum.values())
              .collect(Collectors.toMap(FriendLinkStatusEnum::getCode, Function.identity()));
    
    private static final Map<String, FriendLinkStatusEnum> STATUS_MAP = 
        Arrays.stream(FriendLinkStatusEnum.values())
              .collect(Collectors.toMap(FriendLinkStatusEnum::getName, Function.identity()));

    public static FriendLinkStatusEnum fromCode(Integer code) {
        // 使用缓存的 Map 进行查找，如果找不到，默认返回 UNKNOWN
        return CODE_MAP.getOrDefault(code, REVIEW);
    }

    public static FriendLinkStatusEnum fromName(String name) {
        // 使用缓存的 Map 进行查找，如果找不到，默认返回 UNKNOWN
        return STATUS_MAP.getOrDefault(name, REVIEW);
    }
}
