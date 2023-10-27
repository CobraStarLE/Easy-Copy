package com.cyser.base.enums;

import java.lang.reflect.Type;

public enum ClassTypeEnum {

    /**
     * （原始/基本类型，也叫raw type）
     * 平常所指的类、枚举、数组(String[]、byte[])、注解，
     * 还包括基本类型（原始类型（primitive type 虚拟机创建 8种int 、float加void）对应的包装引用类型Integer、Float等等
     */
    Class,

    /**
     * （带有范型数组类型）
     * 并不是我们工作中所使用的数组String[]、byte[]（这种都属于Class），而是带有泛型的数组即含有< >或者T U等的数组，
     * 即T[] 或者List< String >[]，GenericArrayType的genericComponentType()方法获取到的是TypeVariable T或者ParameterizedType List< String >
     */
    GenericArrayType,

    /**
     * （参数化类型）
     * 就是我们平常所用到的带< >符号的泛型，如List< Student >、Map<String,String>（注意和TypeVariable的区别），
     * ParameterizedType的getRawType是List,Map; getActualTypeArguments是Student和 String String；getOwnerType是找内部类定义在哪个类中
     */
    ParameterizedType,

    /**
     * （类型变量）
     * 比如List< T >中的T,U,V等。TypeVariable有getBounds()界限数组和getGenericDeclaration()定义在哪个类中的方法，
     * 而List< T >是ParameterizedType,ParameterizedType的getRawType是List; getActualTypeArguments是T,T是TypeVariable
     */
    TypeVariable,

    /**
     * （ 通配符类型）
     * 例如List< ? extends Number>中的? extends Number，WildcardType有上下界方法。
     * 而List< ? extends Number>本身是ParameterizedType，ParameterizedType的getRawType()方法获取到的是List;
     * getActualTypeArguments是? extends Number,? extends Number是WildcardType
     */
    WildcardType,
    Unknown;

    public static ClassTypeEnum valueOf(Type type){
        if(type instanceof java.lang.Class<?>){
            return Class;
        }else if(type instanceof java.lang.reflect.ParameterizedType){
            return ClassTypeEnum.ParameterizedType;
        }else if(type instanceof java.lang.reflect.GenericArrayType){
            return ClassTypeEnum.GenericArrayType;
        }else if(type instanceof java.lang.reflect.TypeVariable<?>){
            return ClassTypeEnum.TypeVariable;
        }else if(type instanceof java.lang.reflect.WildcardType){
            return ClassTypeEnum.WildcardType;
        }else{
            return Unknown;
        }
    }
}
