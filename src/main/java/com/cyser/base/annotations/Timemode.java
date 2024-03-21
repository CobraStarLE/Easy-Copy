package com.cyser.base.annotations;


import com.cyser.base.enums.TimeMode;

public @interface Timemode {

    TimeMode value() default
            TimeMode.CURRENT;
}
