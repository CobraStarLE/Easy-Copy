package com.cyser.base.utils;

import com.cyser.base.bean.EnumInfo;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.List;

public class EnumUtil {

    private EnumUtil(){}

    public static Enum getInstance(Class enum_clazz,String fieldName,Object fieldValue) throws IllegalAccessException {
        List<? extends Enum> enum_instances = EnumUtils.getEnumList(enum_clazz);
        if(ObjectUtils.isNotEmpty(enum_instances)){
            Field field= FieldUtils.getField(enum_clazz,fieldName,true);
            if(ObjectUtils.isEmpty(field)){
                throw new RuntimeException("枚举类不存在字段["+fieldName+"]！");
            }
            for (Enum instance:enum_instances){
                Object instance_field_value=FieldUtils.readField(field,instance,true);
                if(String.valueOf(instance_field_value).equals(String.valueOf(fieldValue))){
                    return instance;
                }

            }
        }
        return null;
    }

    public static Object getValue(Class enum_clazz,Enum instance,String fieldName) throws IllegalAccessException, ClassNotFoundException {
        Field field= FieldUtils.getField(enum_clazz,fieldName,true);
        if(ObjectUtils.isEmpty(field)){
            throw new RuntimeException("枚举类不存在字段["+fieldName+"]！");
        }
        Object field_value=FieldUtils.readField(field,instance,true);
        Class field_clazz= ClassUtils.getClass(field.getType().getTypeName());
        if(ClassUtils.isPrimitiveOrWrapper(field_clazz)){
            return BeanUtil.parsePrimitiveOrWrapperOrStringType(field_value,ClassUtils.primitiveToWrapper(field_clazz));
        }
        return field_value;
    }
}
