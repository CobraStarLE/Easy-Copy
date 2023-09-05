package com.cyser.base.enums;

import java.lang.reflect.Type;

public enum ClassTypeEnum {
    Class,
    GenericArrayType,
    ParameterizedType,
    TypeVariable,
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
