package com.cyser.base.bean;

import com.cyser.base.enums.ClassTypeEnum;
import com.cyser.base.enums.DataTypeEnum;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public abstract class CopyDefinition {

    /**
     * 类类型
     */
    public ClassTypeEnum class_type;

    /**
     * 数据类型
     */
    @Setter
    public DataTypeEnum data_type;

    /**
     * 原始类型
     */
    public Type raw_type;

    /**
     * 原始类
     */
    public Class raw_Type_class;

    /**
     * 运行时类型
     */
    public Class runtime_class;

    /**
     * 是否是泛型类型
     */
    public boolean isGeneric = false;

    /**
     * 是否是基本类型
     */
    public boolean isPrimitive = false;

    /**
     * 是否是数组
     */
    public boolean isArray=false;
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
    public boolean isSerializable = false;

}

