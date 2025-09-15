package com.baofeng.blog.exception;
public class AuthException extends RuntimeException {  // 继承RuntimeException
    private final int statusCode;

    public AuthException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    
} 