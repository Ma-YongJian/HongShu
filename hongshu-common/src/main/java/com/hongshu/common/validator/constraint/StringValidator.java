package com.hongshu.common.validator.constraint;

import com.hongshu.common.annotation.NotBlank;
import com.hongshu.common.utils.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 判断是否为空字符串【校验器】
 *
 * @Author hongshu
 */
public class StringValidator implements ConstraintValidator<NotBlank, String> {

    @Override
    public void initialize(NotBlank constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || StringUtils.isBlank(value) || StringUtils.isEmpty(value.trim())) {
            return false;
        }
        return true;
    }
}
