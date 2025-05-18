package com.hongshu.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @Author hongshu
 * web端工具类
 */
@Slf4j
public class WebUtils {

    private WebUtils() {
    }

    /**
     * 获取请求头中的内容
     *
     * @param headerName 请求头name
     * @return 请求内容
     */
    public static String getRequestHeader(String headerName) {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        if (Objects.isNull(ra)) {
            log.error("服务里RequestAttributes对象为空");
            return null;
        }
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();
        return request.getHeader(headerName);
    }
}
