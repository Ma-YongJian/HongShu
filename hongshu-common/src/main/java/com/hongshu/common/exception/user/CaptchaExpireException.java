package com.hongshu.common.exception.user;

/**
 * 验证码失效异常类
 *
 * @Author hongshu
 */
public class CaptchaExpireException extends UserException {

    private static final long serialVersionUID = 1L;

    public CaptchaExpireException() {
        super("user.jcaptcha.expire", null);
    }
}
