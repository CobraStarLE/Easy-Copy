package com.cyser.base.enums;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

public enum TypeEnum {
    Class,
    GenericArrayType,
    ParameterizedType,
    TypeVariable,
    WildcardType,
    Unknown;

    public static TypeEnum valueOf(Type type){
        if(type instanceof java.lang.Class<?>){
            return Class;
        }else if(type instanceof java.lang.reflect.ParameterizedType){
            return TypeEnum.ParameterizedType;
        }else if(type instanceof java.lang.reflect.GenericArrayType){
            return TypeEnum.GenericArrayType;
        }else if(type instanceof java.lang.reflect.TypeVariable<?>){
            return TypeEnum.TypeVariable;
        }else if(type instanceof java.lang.reflect.WildcardType){
            return TypeEnum.WildcardType;
        }else{
            return Unknown;
        }
    }
}
