package com.baofeng.blog.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.baofeng.blog.vo.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
    //框架自动抛出异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity.badRequest().body(ApiResponse.error(400, errorMessage));
    }
    //自定义重复异常
    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<ApiResponse<?>> handleDuplicateUserException(DuplicateUserException ex) {
        return ResponseEntity.badRequest().body(ApiResponse.error(400, ex.getMessage()));
    }
    // 自定义认证异常
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(AuthException ex){
        return ResponseEntity.badRequest().body(ApiResponse.error(ex.getStatusCode(), ex.getMessage()));
    }

    // 空指针等运行时异常
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiResponse<?>> handleNpe(NullPointerException ex) {
        return ResponseEntity.status(500).body(ApiResponse.error(500, "服务器内部错误"));
    }

    // 所有未处理的异常（兜底）
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGenericException(Exception ex) {
        System.out.println(ex.getMessage());
        return ResponseEntity.status(500).body(ApiResponse.error(500, "发生未知错误"));
    }
    

} 