package com.cyser.base;

import com.cyser.base.enums.TypeEnum;
import com.cyser.base.utils.TimestampUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;

public class ClassDefinition {

    public Class clazz;

    public TypeEnum type;

    public Type rawType;

    public Type genericType;

    public Class raw_type_class;

    /**
     * 当为ParameterizedType
     */
    public TypeDefinition parameterType;


    /**
     * 当为GenericArrayType时
     */
    public TypeDefinition genericComponentType;

    /**
     * 是否是泛型类型
     */
    public boolean isGeneric = false;

    /**
     * 当为数组时，数组类型的类定义
     */
    public ClassDefinition componentClassDefine;

    /**
     * 是否是数组
     */
    public boolean isArray = false;

    /**
     * 是否是基本类型
     */
    public boolean isPrimitive = false;

    /**
     * 是否是基本封装类型
     */
    public boolean isPrimitiveWrapper = false;

    /**
     * 是否是基本类型或者基本封装类型
     */
    public boolean isPrimitiveOrWrapper = false;

    /**
     * 是否可序列化
     */
    public boolean isSerializable = true;
}
