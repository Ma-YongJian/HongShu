package com.hongshu.common.exception.exceptionType;

/**
 * 自定义异常类型是方便全局异常拦截器将参数异常拦截，做统一的Response响应编码.如(20,"Parameter Error")
 *
 * @Author hongshu
 */
public class ApiInvalidParamException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ApiInvalidParamException() {
        super();
    }

    public ApiInvalidParamException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApiInvalidParamException(String message) {
        super(message);
    }

    public ApiInvalidParamException(Throwable cause) {
        super(cause);
    }
}
