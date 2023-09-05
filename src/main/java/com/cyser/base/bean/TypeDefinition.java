package com.cyser.base.bean;

import com.cyser.base.enums.ClassTypeEnum;

import java.lang.reflect.Type;
import java.util.Map;

public class TypeDefinition implements CopyDefinition{

    public ClassTypeEnum type;

    public Type rawType;

    public Class rawClass;

    /**
     * 当为ParameterizedType
     */
    public TypeDefinition[] parameter_type_Defines;
    public Map<String,Class> parameter_type_corresponds;//类上范型真实对应，比如我用定义的类Cat<T> ,然后真实使用是new Cat<Color>,这里存的就是T->Color.class


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
