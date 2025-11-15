package com.baofeng.blog.util;

import com.baofeng.blog.config.JwtPropertiesConfig;
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
public class JwtTokenProviderUtil {
    private static final Logger log = LoggerFactory.getLogger(JwtTokenProviderUtil.class);
    private final String secret;
    public JwtTokenProviderUtil(JwtPropertiesConfig jwtProperties) {
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
    public boolean isTokenExpired(Object tokenOrClaims) {
        Claims claims = null;

        if (tokenOrClaims instanceof String token) {
            try {
                claims = parseToken(token);
            } catch (Exception e) {
                // token 解析失败，视为已过期
                return true;
            }
        } else if (tokenOrClaims instanceof Claims c) {
            claims = c;
        } else {
            throw new IllegalArgumentException("参数必须是 token(String) 或 claims(Claims)");
        }

        if (claims == null) {
            return true;
        }

        Date expiredDate = claims.getExpiration();
        return expiredDate == null || !expiredDate.after(new Date());
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