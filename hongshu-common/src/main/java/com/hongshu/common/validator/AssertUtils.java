package com.hongshu.common.validator;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import com.hongshu.common.enums.ResultCodeEnum;
import com.hongshu.common.exception.web.HongshuException;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 校验工具类
 *
 * @Author hongshu
 */
public class AssertUtils {

    public static void isBlank(String str, String... params) {
        isBlank(str, ResultCodeEnum.NOT_NULL.getCode(), params);
    }

    public static void isBlank(String str, Integer code, String... params) {
        if (code == null) {
            throw new HongshuException(ResultCodeEnum.NOT_NULL.getCode(), "code");
        }

        if (StringUtils.isBlank(str)) {
            throw new HongshuException(code, params);
        }
    }

    public static void isNull(Object object, String... params) {
        isNull(object, ResultCodeEnum.NOT_NULL.getCode(), params);
    }

    public static void isNull(Object object, Integer code, String... params) {
        if (code == null) {
            throw new HongshuException(ResultCodeEnum.NOT_NULL.getCode(), "code");
        }

        if (object == null) {
            throw new HongshuException(code, params);
        }
    }

    public static void isArrayEmpty(Object[] array, String... params) {
        isArrayEmpty(array, ResultCodeEnum.NOT_NULL.getCode(), params);
    }

    public static void isArrayEmpty(Object[] array, Integer code, String... params) {
        if (code == null) {
            throw new HongshuException(ResultCodeEnum.NOT_NULL.getCode(), "code");
        }

        if (ArrayUtil.isEmpty(array)) {
            throw new HongshuException(code, params);
        }
    }

    public static void isListEmpty(List<?> list, String... params) {
        isListEmpty(list, ResultCodeEnum.NOT_NULL.getCode(), params);
    }

    public static void isListEmpty(List<?> list, Integer code, String... params) {
        if (code == null) {
            throw new HongshuException(ResultCodeEnum.NOT_NULL.getCode(), "code");
        }

        if (CollUtil.isEmpty(list)) {
            throw new HongshuException(code, params);
        }
    }

    public static void isMapEmpty(Map map, String... params) {
        isMapEmpty(map, ResultCodeEnum.NOT_NULL.getCode(), params);
    }

    public static void isMapEmpty(Map map, Integer code, String... params) {
        if (code == null) {
            throw new HongshuException(ResultCodeEnum.NOT_NULL.getCode(), "code");
        }

        if (MapUtil.isEmpty(map)) {
            throw new HongshuException(code, params);
        }
    }
}
