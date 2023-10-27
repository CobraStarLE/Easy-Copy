package com.cyser.base.annotations;

import com.cyser.base.utils.TimestampUtil;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TimeFormat {
    TimestampUtil.FastDateFormatPattern value() default
            TimestampUtil.FastDateFormatPattern.ISO_DATETIME_FORMAT;
}
