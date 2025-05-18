package com.hongshu.common.annotation;

import com.hongshu.common.validator.Messages;
import com.hongshu.common.validator.constraint.RangValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * 字符串范围约束，限制长度【注解】
 *
 * @Author hongshu
 */
@Target({TYPE, ANNOTATION_TYPE, FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {RangValidator.class})
public @interface Range {

    long min() default 0;

    long max() default Long.MAX_VALUE;

    String message() default Messages.CK_RANGE_DEFAULT;

    String value() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
