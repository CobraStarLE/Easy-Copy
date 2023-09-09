package com.cyser.base.enums;

import com.cyser.base.utils.ClassUtil;
import org.apache.commons.lang3.ClassUtils;

public enum DataTypeEnum {

    Object_Class,//Object类
    Entity_Class,//实体类
    Collection,//集合
    Map,//Hash表
    Array,//数组
    PrimitiveOrWrapper,//基本类型或者封装类型
    Unknown;//未知

    public static DataTypeEnum valueOf(Class clazz){
        if(clazz==Object.class){
            return Object_Class;
        }
        if(ClassUtils.isPrimitiveOrWrapper(clazz)){
            return PrimitiveOrWrapper;
        }
        if(clazz.isArray()){
            return Array;
        }
        if(java.util.Collection.class.isAssignableFrom(clazz)){
            return Collection;
        }
        if(java.util.Map.class.isAssignableFrom(clazz)){
            return Map;
        }
        if(ClassUtil.isSerializableClass(clazz)){
            return Entity_Class;
        }
        return Unknown;
    }
}
