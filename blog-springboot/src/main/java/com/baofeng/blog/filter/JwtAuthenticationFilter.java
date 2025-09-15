package com.baofeng.blog.filter;

import com.baofeng.blog.service.CustomUserDetailsService;
import com.baofeng.blog.util.JwtTokenProvider;
import com.baofeng.blog.vo.ResponseUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.Claims;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, CustomUserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        // 从请求头中提取 token，要求带上 "Bearer " 前缀
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // System.out.println("携带Bearer");
            // System.out.println("Received Authorization Header: " + authHeader);
            String token = authHeader.substring(7); // 去除 "Bearer " 前缀获取真正的 token
            // 验证 token 是否有效（包括签名和过期时间）
            if (jwtTokenProvider.isTokenExpired(token)) {
                //解析token
                Claims claims = jwtTokenProvider.parseToken(token);
                
                // 只允许 Access Token 访问受保护资源
                String tokenType = claims.get("type", String.class);
                if ("access".equals(tokenType)) {
                    String username = claims.get("username",String.class);
                    
                    if (username != null) {
                        // 根据用户名加载用户详细信息
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                        // 可选：进一步验证 token 与 userDetails 是否匹配
                        if (userDetails != null && jwtTokenProvider.validateToken(token, userDetails)) {
                            // 构建认证对象，并存入 SecurityContext 中
                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        } else {
                            ResponseUtil.sendErrorResponse(response, 400, "Access  Token解析的用户不存在");
                        }
                    }
                } else {
                    ResponseUtil.sendErrorResponse(response, 403, "Refresh Token 不能访问受保护资源");
                    return;
                }
            } else {
                ResponseUtil.sendErrorResponse(response, 400, "Token 失效");
                return;

            }
        }
        // 继续执行后续过滤器
        filterChain.doFilter(request, response);
    }
} 