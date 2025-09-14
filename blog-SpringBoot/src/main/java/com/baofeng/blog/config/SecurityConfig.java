package com.baofeng.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.baofeng.blog.filter.JwtAuthenticationFilter;
import com.baofeng.blog.service.CustomUserDetailsService;
import com.baofeng.blog.util.JwtTokenProvider;
// import com.baofeng.blog.exception.CustomAccessDeniedHandler;
// import com.baofeng.blog.exception.CustomAuthenticationEntryPoint;
// import org.springframework.beans.factory.annotation.Autowired;
//博客前台访问不需要token
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // @Autowired
    // private CustomAccessDeniedHandler customAccessDeniedHandler;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/admin/users/register",
                    "/api/admin/users/login",
                    "/api/admin/users/refreshToken",
                    "/api/front/**",
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs",
                    "/v3/api-docs/**",
                    "/v3/api-docs.yaml"
                    ).permitAll()
                .anyRequest().authenticated() // 需要身份验证的请求
            )
            .csrf(csrf -> csrf.disable())
            .httpBasic(httpBasic -> httpBasic.disable())
            .formLogin(form -> form.disable())
            // 在请求被用户名密码的过滤器处理之前，就先执行你的 jwtAuthenticationFilter()，从而实现对请求中 JWT Token 的提取、验证和认证
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);// 添加 JWT 过滤器
            // .exceptionHandling(exceptionHandling -> 
            //      exceptionHandling.accessDeniedHandler(customAccessDeniedHandler)
            // );
                // .authenticationEntryPoint(customAuthenticationEntryPoint) // 处理401
        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider,
                                                       CustomUserDetailsService userDetailsService) {
        return new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
} 