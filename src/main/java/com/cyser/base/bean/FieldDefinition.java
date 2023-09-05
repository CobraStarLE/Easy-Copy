package com.cyser.base.bean;

import com.cyser.base.utils.TimestampUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;

public class FieldDefinition extends CopyDefinition{

    public Field field;

    public Type genericType;

    /**
     * 当字段类型为范型时，字段范型的运行时类型
     */
    public Class[] parameter_Type_classes;

    public TimestampUtil.FastDateFormatPattern timeFormat;

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
        return "FieldDefinition{"
               + "field="
               + field
               + ", type="
               + raw_type
               + ", genericType="
               + genericType
               + ", raw_Type_class="
               + raw_Type_class
               + ", parameter_Type_classes="
               + Arrays.toString(parameter_Type_classes)
               + ", isGeneric="
               + isGeneric
               + ", isPrimitive="
               + isPrimitive
               + ", isPrimitiveWrapper="
               + isPrimitiveWrapper
               + ", isTime="
               + isTime
               + ", isSerializable="
               + isSerializable
               + '}';
    }
}
