package com.baofeng.blog.vo;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ResponseUtil {

    /**
     * 统一返回 JSON 格式的错误信息
     * @param response HttpServletResponse 对象
     * @param statusCode HTTP 状态码
     * @param message 错误消息
     * @throws IOException
     */
    public static void sendErrorResponse(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setContentType("application/json; charset=UTF-8");
        response.setStatus(statusCode);
        
        ApiResponse<Void> errorResponse = ApiResponse.error(statusCode, message);
        String json = new ObjectMapper().writeValueAsString(errorResponse);
        
        response.getWriter().write(json);
        response.getWriter().flush();
    }
}
