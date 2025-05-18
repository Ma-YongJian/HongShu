package com.hongshu.common.enums;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author hongshu
 */
@ApiModel("统一返回结果")
@Data
public class Result<T> {

    @ApiModelProperty("状态码")
    private Integer code;

    @ApiModelProperty("信息")
    private String message;

    @ApiModelProperty("数据")
    private T data;

    //构造私有化
    private Result() {
    }

    //设置数据,返回对象的方法
    public static <T> Result<T> build(T data, Integer code, String message) {
        //创建Resullt对象，设置值，返回对象
        Result<T> result = new Result<>();
        //判断返回结果中是否需要数据
        if (data != null) {
            //设置数据到result对象
            result.setData(data);
        }
        //设置其他值
        result.setCode(code);
        result.setMessage(message);
        //返回设置值之后的对象
        return result;
    }


    //设置数据,返回对象的方法
    public static <T> Result<T> build(T data, ResultCodeEnum resultCodeEnum) {
        //创建Resullt对象，设置值，返回对象
        Result<T> result = new Result<>();
        //判断返回结果中是否需要数据
        if (data != null) {
            //设置数据到result对象
            result.setData(data);
        }
        //设置其他值
        result.setCode(resultCodeEnum.getCode());
        result.setMessage(resultCodeEnum.getMessage());
        //返回设置值之后的对象
        return result;
    }

    //成功的方法
    public static <T> Result<T> ok(T data) {
        return build(data, ResultCodeEnum.SUCCESS);
    }

    public static <T> Result<T> ok() {
        return build(null, ResultCodeEnum.SUCCESS);
    }

    public static <T> Result<T> build(Integer code, String message) {
        //创建Resullt对象，设置值，返回对象
        Result<T> result = new Result<>();
        //设置其他值
        result.setCode(code);
        result.setMessage(message);
        //返回设置值之后的对象
        return result;
    }

    //失败的方法
    public static <T> Result<T> fail(T data) {
        return build(data, ResultCodeEnum.FAIL);
    }
}
