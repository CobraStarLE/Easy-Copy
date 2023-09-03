package com.cyser.base;

import com.cyser.base.enums.TypeEnum;

import java.lang.reflect.Type;

public class TypeDefinition {

    public TypeEnum type;

    public Type rawType;

    public Class rawClass;

    /**
     * 当为ParameterizedType
     */
    public TypeDefinition[] parameter_type_Defines;

    /**
     * 是否是泛型类型
     */
    public boolean isGeneric = false;

    /**
     * 当为Class时，并且是数组时，数组类型的类定义
     */
    public ClassDefinition componetClassDefine;

    /**
     * 当为WildcardType时
     */
    public ClassDefinition upperBounds;
    public ClassDefinition lowerBounds;

    /**
     * 当为GenericArrayType时
     */
    public TypeDefinition genericComponentType;

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
