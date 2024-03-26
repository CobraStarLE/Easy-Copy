package com.cyser.base.bean;

import com.cyser.base.enums.DataTypeEnum;
import com.cyser.base.enums.FastDateFormatPattern;
import com.cyser.base.enums.TimeMode;
import org.apache.commons.lang3.ObjectUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author wangjinsheng
 */
public class FieldDefinition extends TypeDefinition {

    public Field field;

    public Type genericType;

    /**
     * 当字段类型为范型时，字段范型的运行时类型
     */
    public Class[] parameter_Type_classes;

    public FastDateFormatPattern timeFormat;

    public TimeMode timeMode;

    /**
     * 是否是时间类型
     */
    public boolean isTime = false;


    /**
     * 如果是枚举类的话，解析到的枚举信息
     */
    public Map<Class, EnumInfo> enumInfos;

    public DataTypeEnum getData_type(Class id) {
        if (isEnum(id)) {
            return DataTypeEnum.Enum;
        }
        return data_type;
    }

    public boolean isEnum(Class id) {
        if (Enum.class.isAssignableFrom(this.runtime_class)) {
            return true;
        }
        EnumInfo enumInfo = getEnumInfo(id);
        if (enumInfo != null) {
            if (ObjectUtils.isNotEmpty(enumInfo.enum_clazz) && enumInfo.enum_clazz.isEnum()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否是枚举（判断依据：1、本身是枚举类，2、字段上有注解EnumFormat,并且enme_class不为空）
     */
    public EnumInfo getEnumInfo(Class id) {
        EnumInfo info = enumInfos.get(id);
        if (info == null) {
            info = enumInfos.get(this.field.getDeclaringClass());
        }
        return info;
    }
}
