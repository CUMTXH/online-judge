// src/main/java/com/ex/boot/util/JwtTokenUtil.java
package com.ex.boot.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.testng.annotations.Test;

import java.security.Key;
import java.util.Date;

public class JwtTokenUtil {

    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    private static final long EXPIRATION_TIME = 86400000; // 24小时（毫秒）

    public static String generateToken(String username, Long userId) {
        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS512)
                .compact();
    }


    public static boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static String getUsernameFromToken(String token) {
        return parseToken(token).getSubject();
    }

    public static Long extractUserId(String token) {
        Claims claims = parseToken(token);
        Object userIdObj = claims.get("userId");
        if (userIdObj instanceof Integer) {
            return ((Integer) userIdObj).longValue();  // 兼容数据库返回 Integer 的情况
        } else if (userIdObj instanceof Long) {
            return (Long) userIdObj;
        } else if (userIdObj instanceof String) {
            return Long.parseLong((String) userIdObj);
        } else {
            throw new IllegalArgumentException("Token 中不包含有效的 userId");
        }
    }


    // 测试令牌生成和验证
    @Test
    public static class JwtTest {
        public static void main(String[] args) {
            String token = JwtTokenUtil.generateToken("tester",1L);
            System.out.println("Token: " + token);
            System.out.println("验证结果: " + JwtTokenUtil.validateToken(token));
        }
    }
}
