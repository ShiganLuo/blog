package com.baofeng.blog.util;

public enum ResultCode {
    SUCCESS(200, "success"),
    PARAM_ERROR(400, "参数错误"),
    NOT_FOUND(404, "资源不存在"),
    SERVER_ERROR(500, "服务器错误"),
    UNAUTHORIZED(403,"权限不足");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }
}