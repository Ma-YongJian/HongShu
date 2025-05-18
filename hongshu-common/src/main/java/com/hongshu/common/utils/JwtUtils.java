package com.hongshu.common.utils;

import com.hongshu.common.constant.TokenConstant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @Author hongshu
 */
@Slf4j
public class JwtUtils {

    //定义两个常量，1.设置过期时间 2.密钥（随机，由公司生成）
    public static final String APP_SECRET = "ukc8BDbRigUDaY6pZFfWus2jZWLPHO";

    private JwtUtils() {
    }

    /**
     * 生成token
     *
     * @param uid
     * @param expirationTime
     * @return
     */
    public static String getJwtToken(String uid, long expirationTime) {
        return Jwts.builder()
                //设置token的头信息
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                //设置过期时间
                .setSubject("user")
                .setIssuedAt(new Date())
                //设置刷新
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                //设置token的主题部分
                .claim("userId", uid)
                //签名哈希
                .signWith(SignatureAlgorithm.HS256, APP_SECRET)
                .compact();
    }

    /**
     * 判断token是否存在与有效
     *
     * @param jwtToken
     * @return
     */
    public static boolean checkToken(String jwtToken) {
        if (StringUtils.isEmpty(jwtToken)) {
            return false;
        }
        try {
            //验证是否有效的token
            Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 根据token信息得到getUserId
     *
     * @param jwtToken
     * @return
     */
    public static String getUserId(String jwtToken) {
        //验证是否有效的token
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
        //得到字符串的主题部分
        Claims claims = claimsJws.getBody();
        return (String) claims.get("userId");
    }

    /**
     * 判断token是否存在与有效
     *
     * @param request
     * @return
     */
    public static boolean checkToken(HttpServletRequest request) {
        try {
            String jwtToken = request.getHeader(TokenConstant.ACCESS_TOKEN);
            if (StringUtils.isEmpty(jwtToken)) {
                return false;
            }
            Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
