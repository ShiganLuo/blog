package com.baofeng.blog.util;

import com.baofeng.blog.config.JwtProperties;
import com.baofeng.blog.entity.User;

import io.jsonwebtoken.Jwts;
import java.util.Date;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {
    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);
    private final String secret;
    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.secret = jwtProperties.getSecret();
    }

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
            return !expiredDate.after(new Date());
        } else {
            return true;
        }

    }

    public boolean isRefreshToken(String token) {
        Claims claims = parseToken(token);
        String type = (String) claims.get("type");
        if (type.equals("refresh")){
            return true;
        }else {
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
            log.info("Token解析失败:{}",token);
            throw new IllegalArgumentException("Token解析失败");
        }
        return claims;
    }

} 