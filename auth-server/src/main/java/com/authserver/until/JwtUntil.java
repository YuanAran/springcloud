package com.authserver.until;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtUntil {
    private static final String SECRET="sdahsdasdaosdjaosidjaodnjnkhasdaidhio";
    private static final long EXPIRATION=60*60*1000;

    //生成token
    public static String generateToken(String username){
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+EXPIRATION))
                .signWith(SignatureAlgorithm.HS256,SECRET)
                .compact();
    }

    //解析token
    public static Claims parseToken(String token){
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJwt(token)
                .getBody();
    }

    // 校验 token 是否有效
    public static boolean isTokenValid(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    // 获取用户名
    public static String getUsername(String token) {
        return parseToken(token).getSubject();
    }
}
