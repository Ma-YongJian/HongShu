package com.hongshu.common.annotation;

import com.hongshu.common.validator.Messages;
import com.hongshu.common.validator.constraint.BooleanValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * 判断Boolean类型是否为空【注解】
 *
 * @Author hongshu
 */
@Target({TYPE, ANNOTATION_TYPE, FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {BooleanValidator.class})
public @interface BooleanNotNULL {

    boolean required() default true;

    String message() default Messages.CK_NOT_NULL_DEFAULT;

    String value() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
