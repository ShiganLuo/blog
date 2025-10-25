package com.baofeng.blog.enums;

public enum ResultCodeEnum {
    SUCCESS(200, "请求成功"),
    CREATED(201,"资源创建成功"),
    BAD_REQUEST(400,"请求参数错误"),
    UNAUTHORIZED(401,"权限不足"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    CONFLICT(409, "资源冲突"),
    UNPROCESSABLE_ENTITY(422, "请求格式正确，但含有语义错误"),
    INTERNEL_SERVER_ERROR(500, "服务器错误"),
    BAD_GATEWAY(502, "网关错误"),
    SERVICE_UNAVAILABLE(503, "服务不可用"),
    GATEWAY_TIMEOUT(504, "网关超时");
    

    private final Integer code;
    private final String message;

    ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer code() {
        return code;
    }

    public String message() {
        return message;
    }
}