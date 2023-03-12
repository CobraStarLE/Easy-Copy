package com.cyser.base;

import com.cyser.base.utils.TimestampUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;

public class FieldDefinition {

    public Field field;

    public Type type;

    public Type genericType;

    public Class raw_Type_class;

    public Class[] parameter_Type_classes;

    public TimestampUtil.FastDateFormatPattern timeFormat;

    /**
     * 是否是泛型类型
     */
    public boolean isGeneric = false;

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
     * 是否是时间类型
     */
    public boolean isTime = false;

    /**
     * 是否可序列化
     */
    public boolean isSerializable = true;

    @Override
    public String toString() {
        return "FieldDefinition{" +
                "field=" + field +
                ", type=" + type +
                ", genericType=" + genericType +
                ", raw_Type_class=" + raw_Type_class +
                ", parameter_Type_classes=" + Arrays.toString(parameter_Type_classes) +
                ", isGeneric=" + isGeneric +
                ", isPrimitive=" + isPrimitive +
                ", isPrimitiveWrapper=" + isPrimitiveWrapper +
                ", isTime=" + isTime +
                ", isSerializable=" + isSerializable +
                '}';
    }
}
