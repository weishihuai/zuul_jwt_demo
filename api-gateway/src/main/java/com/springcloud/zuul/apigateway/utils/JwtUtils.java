package com.springcloud.zuul.apigateway.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class JwtUtils {

    /**
     * 签名用的密钥
     */
    private static final String SIGNING_KEY = "u3HYQHDdH8JBblN0Jyhu4Fy9IMXEiilM";

    /**
     * 用户登录成功后生成Jwt token
     * 使用Hs256算法
     *
     * @param exp    jwt过期时间
     * @param claims 保存在Payload（有效载荷）中的内容
     * @return token字符串
     */
    public String createJwtToken(Date exp, Map<String, Object> claims) {
        //token生成的时间，默认取当前时间
        Date now = new Date(System.currentTimeMillis());

        //创建一个JwtBuilder，设置jwt的body
        JwtBuilder builder = Jwts.builder()
                //保存在Payload（有效载荷）中的内容, 自定义一些数据保存在这里
                .setClaims(claims)
                //iat: jwt的签发时间
                .setIssuedAt(now)
                //设置过期时间
                .setExpiration(exp)
                //使用HS256算法和签名使用的秘钥生成密文
                .signWith(SignatureAlgorithm.HS256, SIGNING_KEY);
        return builder.compact();
    }

    /**
     * 解析token，获取到Payload（有效载荷）中的内容，包括验证签名，判断是否过期
     *
     * @param token 令牌
     * @return
     */
    public Claims parseJwtToken(String token) {
        //得到DefaultJwtParser
        return Jwts.parser()
                //设置签名的秘钥
                .setSigningKey(SIGNING_KEY)
                //设置需要解析的token
                .parseClaimsJws(token).getBody();
    }

}
