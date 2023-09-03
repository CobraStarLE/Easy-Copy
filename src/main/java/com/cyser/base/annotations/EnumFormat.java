package com.cyser.base.annotations;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnumFormat {

    Class enum_class();

    String field_from() default "name";

    String field_local() default "name";
}
