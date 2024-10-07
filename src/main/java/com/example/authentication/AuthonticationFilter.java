package com.example.authentication;

import com.example.constant.Constant;
import com.example.exception.BusinessException;
import com.example.utils.JwtProcessor;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.PathMatcher;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import static com.example.constant.Constant.ALLOWED_PATHS;

/**
 * [一句话描述该类的功能]
 *
 * @author : [24360]
 * @version : [v1.0]
 * @createTime : [2024/9/24 16:54]
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthonticationFilter implements Filter {
    private final JwtProcessor jwtProcessor;
    private final CurrentUserHolder currentUserHolder;
    private final PathMatcher pathMatcher;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String accessToken = request.getHeader(Constant.AUTHORIZATION);
        String requestURI = request.getRequestURI();
        try {
            for (String path : ALLOWED_PATHS) {
                if (pathMatcher.match(path, requestURI)) {
                    filterChain.doFilter(request, response);
                    return;
                }
            }
            Map<String, Object> userMap = jwtProcessor.extractUserMap(accessToken);
            if (userMap == null || !userMap.containsKey(Constant.USER_MAP_KEY_ID)
                    || StringUtils.isBlank((CharSequence) userMap.get(Constant.USER_MAP_KEY_ID))
                    || !userMap.containsKey(Constant.USER_MAP_KEY_NICK_NAME)
                    || StringUtils.isBlank((CharSequence) userMap.get(Constant.USER_MAP_KEY_NICK_NAME))
                    || !userMap.containsKey(Constant.USER_MAP_KEY_ACCOUNT)
                    || StringUtils.isBlank((CharSequence) userMap.get(Constant.USER_MAP_KEY_ACCOUNT))) {
                log.error("令牌解析失败");
                throw new BusinessException("非法的令牌格式", new MalformedJwtException("非法的令牌格式"));
            }
            Long userId = Long.valueOf(String.valueOf(userMap.get(Constant.USER_MAP_KEY_ID)));
            currentUserHolder.setUserId(userId);
            filterChain.doFilter(request, response);
        } finally {
            currentUserHolder.clear();
        }
    }
}
