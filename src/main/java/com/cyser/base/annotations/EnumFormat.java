package com.cyser.base.annotations;

import java.lang.annotation.*;

/**
 * 枚举注解，用来标识一个基本类型或者封装类型或者String类型的字段为枚举类型
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnumFormat {

    /**
     * 当在对象复制时，该id一般为对立字段所在的类Class，如果不写的话就默认时当前字段所在类Class
     * @return
     */
    Class id() default Object.class;

    /**
     * 对应的枚举类型，如果字段本身是枚举类型，此属性可不填，默认是字段对应的枚举类型
     * @return
     */
    Class enum_class() default Object.class;

    /**
     * 在复制源对象对应的字段时，该属性用来标识被复制对象对应的枚举的字段名，如果字段本身是枚举类型，此属性可不填
     * <br/>
     * 你可以理解为，这个属性标识的是我要复制哪个枚举类的哪个字段
     * @return
     */
    String from_field() default "";

    /**
     * 代表当前枚举的字段名称，如果字段本身是枚举类型，此属性可不填
     * @return
     */
    String self_field() default "";

    /**
     * 代表我要复值给别的对象时当前枚举实例对应的字段
     * @return
     */
    String to_field() default "";
}
