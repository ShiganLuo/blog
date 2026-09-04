package com.baofeng.blog.exception;
import com.baofeng.blog.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import org.springframework.stereotype.Component;
import org.springframework.security.web.AuthenticationEntryPoint;
import java.io.IOException;
import org.springframework.security.core.AuthenticationException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json; charset=UTF-8");
        // 保持HTTP 200，业务码401，与JWT过滤器行为一致
        ApiResponse<Void> errorResponse = ApiResponse.error(401, "认证令牌无效或已过期，请重新登录");
        String json = new ObjectMapper().writeValueAsString(errorResponse);
        httpServletResponse.getWriter().write(json);
        httpServletResponse.getWriter().flush();
    }
}
