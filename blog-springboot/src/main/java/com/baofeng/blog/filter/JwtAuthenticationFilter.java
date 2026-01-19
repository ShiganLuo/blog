package com.baofeng.blog.filter;

import com.baofeng.blog.service.CustomUserDetailsService;
import com.baofeng.blog.common.util.JwtTokenProviderUtil;
import com.baofeng.blog.dto.ResponseUtil;
import com.baofeng.blog.enums.ResultCodeEnum;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.List;
import io.jsonwebtoken.Claims;


public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProviderUtil jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final List<String> whiteListUris;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(JwtTokenProviderUtil jwtTokenProvider, CustomUserDetailsService userDetailsService, List<String> whiteListUris) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
        this.whiteListUris = whiteListUris;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String requestUri = request.getRequestURI();
        for (String uri : whiteListUris) {
            if (requestUri.startsWith(uri.replace("/**", ""))) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // System.out.println("携带Bearer");
            // System.out.println("Received Authorization Header: " + authHeader);
            String token = authHeader.substring(7); // 去除 "Bearer " 前缀获取真正的 token
            
            // 验证 token 是否有效（包括签名和过期时间）
            Claims claims;
            try {
                claims = jwtTokenProvider.parseToken(token);
            } catch (Exception e) {
                ResponseUtil.sendErrorResponse(response, ResultCodeEnum.UNAUTHORIZED, "token解析失败");
                return;
            }

            if (!jwtTokenProvider.isTokenExpired(claims)) {
                
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
                            ResponseUtil.sendErrorResponse(response, ResultCodeEnum.BAD_REQUEST, "请求参数错误");
                            logger.warn("accessToken解析的用户不存在");
                            return;
                        }
                    }
                } else {
                    ResponseUtil.sendErrorResponse(response, ResultCodeEnum.UNAUTHORIZED, "Refresh Token 不能访问受保护资源");
                    logger.warn("Refresh Token 不能访问受保护资源");
                    return;
                }
            } else {
                ResponseUtil.sendErrorResponse(response, ResultCodeEnum.UNAUTHORIZED, "Token 失效");
                logger.warn("Token 失效");
                return;

            }
        } else {
            ResponseUtil.sendErrorResponse(response, ResultCodeEnum.UNAUTHORIZED, "请求未携带accessToken");
            logger.warn("请求未携带accessToken");
            return;
        }

        
        // 继续执行后续过滤器
        filterChain.doFilter(request, response);
    }
} 