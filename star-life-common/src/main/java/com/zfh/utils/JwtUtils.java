package com.zfh.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Map;
import java.util.UUID;

/**
 * jwt工具类
 */
public class JwtUtils {
    /**
     * 生成安全的JWT密钥
     */
    public static SecretKey generateSecureKey(String secret) {
        // 确保密钥长度足够，如果不够则进行处理
        if (secret.length() < 32) {
            // 可以通过重复字符串或使用其他方式扩展密钥
            secret = String.format("%-32s", secret).replace(' ', 'x'); // 用x填充到32位
        }
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 生成JWT
     */
    public static String generateToken(String key, Map<String, Object> claims) {
        // 令牌id
        String uuid = UUID.randomUUID().toString();

        // 使用安全的密钥生成方式
        SecretKey secretKey = generateSecureKey(key);

        return Jwts.builder()
                // 设置头部信息header
                .header()
                .add("typ", "JWT")
                .add("alg", "HS256")
                .and()
                // 设置自定义负载信息payload
                .claims(claims)
                // 令牌ID
                .id(uuid)
                // 签名
                .signWith(secretKey) // 使用安全密钥
                .compact();
    }

    /**
     * 解析JWT
     */
    public static Jws<Claims> parseToken(String key, String token) {
        SecretKey secretKey = generateSecureKey(key);

        return Jwts.parser()
                .verifyWith(secretKey) // 使用安全密钥
                .build()
                .parseSignedClaims(token);
    }

}
