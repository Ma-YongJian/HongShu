package com.hongshu.common.enums;

import lombok.Getter;

/**
 * 统一返回结果状态信息类
 *
 * @Author hongshu
 */
@Getter
public enum ResultCodeEnum {

    SUCCESS(200, "成功"),
    FAIL(201, "失败"),
    TOKEN_FAIL(401, "token异常"),
    TOKEN_EXIST(501, "token过期"),
    ERROR_PASSWORD(502, "密码有误，请检查重新输入"),

    NOT_NULL(10001, "为空");

    private final Integer code;

    private final String message;

    private ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
