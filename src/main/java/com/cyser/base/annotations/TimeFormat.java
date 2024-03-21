package com.cyser.base.annotations;

import com.cyser.base.enums.FastDateFormatPattern;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TimeFormat {

    FastDateFormatPattern value() default
            FastDateFormatPattern.ISO_DATETIME_FORMAT;
}
