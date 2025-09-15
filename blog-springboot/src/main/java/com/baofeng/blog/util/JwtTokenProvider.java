package com.baofeng.blog.util;

import com.baofeng.blog.entity.User;

import io.jsonwebtoken.Jwts;
import java.util.Date;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {
    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);
    // @Value注解从application.yml中获取
    @Value("${jwt.secret}")
    private String secret;
    // 默认token有效时间
    @Value("${jwt.expiration}")
    private long expiration;
    /**
     * 从token中获取登录用户名
     */
    public String getUserNameFromToken(String token) {
        String username;
        try {
            Claims claims = parseToken(token);
            // username =  claims.getSubject();
            // 从 claims 中获取 "username" 的值
           username = (String) claims.get("username");
        } catch (Exception e) {
            username = null;
        }
        return username;
    }
   /**
     * 校验token, 检验token解析出来的用户是否存在于数据库中
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        String username = getUserNameFromToken(token);
        return username.equals(userDetails.getUsername());
    }

    /**
     * 判断token是否已经失效
     */
    public boolean isTokenExpired(String token) {
        Claims claims = parseToken(token);
        if ( claims != null) {
            Date expiredDate = claims.getExpiration();
            return expiredDate.after(new Date());
        } else {
            return false;
        }

    }

    public String generateToken(User user, long expiration, boolean isRefreshToken) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .claim("type", isRefreshToken ? "refresh" : "access")
                .claim("username", user.getUsername())
                .claim("role", user.getRole())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    public Claims parseToken(String token) {
        Claims claims = null;
        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(secret.getBytes(StandardCharsets.UTF_8))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.info("JWT格式验证失败:{}",token);
        }
        return claims;
    }

} 