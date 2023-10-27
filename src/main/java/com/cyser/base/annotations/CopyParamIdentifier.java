package com.cyser.base.annotations;

import java.lang.annotation.*;

/**
 * 该注解是为了标识拷贝对象时用到哪个CopyParam，可以用在拷贝Class以及字段上
 */
@Target({ElementType.TYPE,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CopyParamIdentifier {

    /**
     * 用来标识所要使用的CopyParam,与CopyParam的id作对应
     * @return
     */
    String id();
}
