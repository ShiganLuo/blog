package com.baofeng.blog.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.enums.ResultCodeEnum;
import com.fasterxml.jackson.databind.JsonMappingException;

@RestControllerAdvice // 异常在 DispatcherServlet 调用 Controller 方法期间发生的
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    //框架自动抛出异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        logger.warn(ex.getMessage());
        return ResponseEntity.badRequest().body(ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR, errorMessage));
    }

    // 业务逻辑中抛出的非法参数异常
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.warn(ex.getMessage());
        return ResponseEntity.badRequest().body(ApiResponse.error(400, ex.getMessage()));
    }

    @ExceptionHandler(JsonMappingException.class)
    public ResponseEntity<ApiResponse<?>> handleJsonMappingException(JsonMappingException ex) {
        Throwable cause = ex.getCause();

        // 构造器里抛的 IllegalArgumentException
        if (cause instanceof IllegalArgumentException) {
            logger.warn("请求参数不合法: {}", cause.getMessage(), cause); // 日志记录
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error(ResultCodeEnum.BAD_REQUEST, cause.getMessage()));
        }

        // 其他 JSON 映射异常
        logger.warn("请求参数格式错误: {}", ex.getMessage(), ex); // 日志记录
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(ResultCodeEnum.BAD_REQUEST, "请求参数格式错误"));
    }

    // 反序列化异常首先会被 Spring 包装成 HttpMessageNotReadableException
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause();

        // 捕获 record 构造器抛出的 IllegalArgumentException
        if (cause != null && cause.getCause() instanceof IllegalArgumentException iae) {
            logger.warn("请求参数不合法: {}", iae.getMessage(), iae); // 日志记录
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error(ResultCodeEnum.BAD_REQUEST, iae.getMessage()));
        }

        // 其他 JSON/HTTP 消息解析异常
        logger.warn("请求参数格式错误: {}", ex.getMessage(), ex); // 日志记录
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(ResultCodeEnum.BAD_REQUEST, "请求参数格式错误"));
    }
    
    //自定义重复异常
    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<ApiResponse<?>> handleDuplicateUserException(DuplicateUserException ex) {
        logger.warn(ex.getMessage());
        return ResponseEntity.badRequest().body(ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR));
    }

    // 空指针等运行时异常
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiResponse<?>> handleNpe(NullPointerException ex) {
        logger.warn(ex.getMessage());
        return ResponseEntity.status(500).body(ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR, "服务器内部错误"));
    }

    // 所有未处理的异常（兜底）
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGenericException(Exception ex) {
        logger.warn(ex.getMessage());
        return ResponseEntity.status(500).body(ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR, "发生未知错误"));
    }
    

} 