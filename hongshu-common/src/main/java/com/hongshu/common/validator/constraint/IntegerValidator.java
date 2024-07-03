package com.hongshu.common.validator.constraint;

import com.hongshu.common.annotation.IntegerNotNull;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 判断Integer是否为空【校验器】
 *
 * @author: hongshu
 * @date 2019年12月4日13:16:06
 */
public class IntegerValidator implements ConstraintValidator<IntegerNotNull, Integer> {

    @Override
    public void initialize(IntegerNotNull constraintAnnotation) {

    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return true;
    }
}
