package com.hongshu.common.exception.exceptionType;

import com.hongshu.common.global.BaseMessageConf;
import com.hongshu.common.global.ErrorCode;

import java.io.Serializable;

/**
 * 自定义登录相关的异常
 *
 * @Author hongshu
 */
public class LoginException extends RuntimeException implements Serializable {

    /**
     * 异常状态码
     */
    private String code;

    public LoginException() {
        super(BaseMessageConf.QUERY_DEFAULT_ERROR);
        this.code = ErrorCode.QUERY_DEFAULT_ERROR;
    }

    public LoginException(String message, Throwable cause) {
        super(message, cause);
        this.code = ErrorCode.QUERY_DEFAULT_ERROR;
    }

    public LoginException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public LoginException(String message) {
        super(message);
        this.code = ErrorCode.QUERY_DEFAULT_ERROR;
    }

    public LoginException(String code, String message) {
        super(message);
        this.code = code;
    }

    public LoginException(Throwable cause) {
        super(cause);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
