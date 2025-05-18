package com.hongshu.web.auth;

import com.hongshu.common.constant.TokenConstant;
import com.hongshu.common.constant.UserConstant;
import com.hongshu.common.enums.ResultCodeEnum;
import com.hongshu.common.exception.ServiceException;
import com.hongshu.common.utils.JwtUtils;
import com.hongshu.common.utils.WebUtils;
import com.hongshu.common.validator.myVaildator.noLogin.NoLoginIntercept;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author hongshu
 */
@Slf4j
public class UserLoginInterceptor implements HandlerInterceptor {

    /**
     * token拦截验证
     *
     * @param request  current HTTP request
     * @param response current HTTP response
     * @param handler  chosen handler to execute, for type and/or instance evaluation
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        // 获取请求路径
        String requestURI = request.getRequestURI();
        // 检查请求路径是否包含 "/web"
        if (requestURI.contains("/web")) {
            String accessToken = request.getHeader(TokenConstant.ACCESS_TOKEN);
            log.info("accessToken:{},{}", accessToken, WebUtils.getRequestHeader(UserConstant.USER_ID));
            //获取方法处理器
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            NoLoginIntercept noLoginIntercept =
                    handlerMethod.getMethod()//这一步是获取到我们要访问的方法
                            //然后根据我们制定的自定义注解的Class对象来获取到对应的注解
                            .getAnnotation(NoLoginIntercept.class);

            if (noLoginIntercept != null) {
                if (!StringUtils.isEmpty(accessToken)) {
                    setLocalUser(accessToken);
                }
                return true;
            }

            //判断token不为空
            if (!StringUtils.isEmpty(accessToken)) {
                setLocalUser(accessToken);
                return true;
            }
            throw new ServiceException(ResultCodeEnum.TOKEN_FAIL.getMessage(), ResultCodeEnum.TOKEN_FAIL.getCode());
        }
        return true;
    }

    private void setLocalUser(String accessToken) {
        boolean flag = JwtUtils.checkToken(accessToken);
        if (!flag) {
            throw new ServiceException(ResultCodeEnum.TOKEN_EXIST.getMessage(), ResultCodeEnum.TOKEN_EXIST.getCode());
        }
        String userId = JwtUtils.getUserId(accessToken);
        AuthContextHolder.setUserId(userId);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e) {
//        log.info("清除UserID");
        AuthContextHolder.removeUserId();
    }
}
