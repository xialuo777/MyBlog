package com.example.utils;

import com.example.constant.Constant;
import com.example.exception.BusinessException;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

/**
 * [JWT工具类]
 *
 * @author : [24360]
 * @version : [v1.0]
 * @createTime : [2024/9/23 15:11]
 */
@Component
@Slf4j
public class JwtProcessor {
    @Value("${security.jwt.secret}")
    private String secretKey;
    @Value("${security.jwt.expiration}")
    private Integer jwtExpiration;

    public String generateToken(Map<String, Object> userMap) {
        final Date date = DateUtils.addMinutes(new Date(), jwtExpiration);
        return Jwts.builder()
                .setClaims(userMap)
                .setExpiration(date)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    public String generateRefreshToken(Map<String, Object> userMap) {
        final Date date = DateUtils.addMinutes(new Date(), jwtExpiration * 4 * 24 * 7);
        return Jwts.builder()
                .setClaims(userMap)
                .setExpiration(date)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    public boolean validateToken(String token, String account) {
        Map<String, Object> userMap = extractUserMap(token);
        return userMap.get(Constant.USER_MAP_KEY_ACCOUNT).equals(account);
    }

    public Map<String, Object> extractUserMap(String token) {
        Map<String, Object> userMap;
        try {
            userMap = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new BusinessException("令牌已过期", e);
        } catch (SignatureException e) {
            throw new BusinessException("令牌校验未通过", e);
        } catch (MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            throw new BusinessException("令牌格式错误", e);
        }
        return userMap;
    }


}
