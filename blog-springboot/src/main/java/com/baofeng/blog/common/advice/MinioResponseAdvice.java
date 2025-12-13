package com.baofeng.blog.common.advice;

import com.baofeng.blog.common.util.minio.MinioUrlConverter;
import com.baofeng.blog.dto.ApiResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
@Component
public class MinioResponseAdvice implements ResponseBodyAdvice<Object> {

    private final MinioUrlConverter converter;

    public MinioResponseAdvice(MinioUrlConverter converter) {
        this.converter = converter;
    }

    @Override
    public boolean supports(MethodParameter returnType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        // 只处理 ApiResponse
        return ApiResponse.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            org.springframework.http.server.ServerHttpRequest request,
            org.springframework.http.server.ServerHttpResponse response) {

        if (body instanceof ApiResponse<?> apiResponse) {
            Object data = apiResponse.result();
            if (data != null) {
                converter.convert(data);
            }
        }

        return body;
    }
}
