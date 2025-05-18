package com.hongshu.common.exception.web;

import com.hongshu.common.enums.ResultCodeEnum;
import com.hongshu.common.utils.MessageUtil;
import lombok.Data;

/**
 * 异常
 *
 * @Author hongshu
 */
@Data
public class HongshuException extends RuntimeException {

    // 异常状态码
    private Integer code;


    /**
     * 通过状态码和错误消息创建异常对象
     *
     * @param message
     * @param code
     */
    public HongshuException(String message, Integer code) {
        super(message);
        this.code = code;
    }


    public HongshuException(int code, String... params) {
        super(MessageUtil.getMessage(code, params));
        this.code = code;
    }

    public HongshuException(String msg) {
        super(msg);
        this.code = ResultCodeEnum.FAIL.getCode();
    }

    /**
     * 接收枚举类型对象
     *
     * @param resultCodeEnum
     */
    public HongshuException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }
}
