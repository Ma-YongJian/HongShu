package com.hongshu.common.exception;

import com.hongshu.common.enums.ResponseEnum;
import lombok.Data;

/**
 * 业务异常
 *
 * @author: hongshu
 * @date: 2020/3/4
 * @version: 3.0.0
 */
@Data
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private Integer code;
    private String msg;

    public BusinessException() {
        this.code = ResponseEnum.BUSINESS_ERROR.getCode();
        this.msg = ResponseEnum.BUSINESS_ERROR.getMsg();
    }

    public BusinessException(String msg) {
        super(msg);
        this.code = ResponseEnum.BUSINESS_ERROR.getCode();
        this.msg = msg;
    }

}
