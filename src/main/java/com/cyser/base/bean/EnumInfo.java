package com.cyser.base.bean;

import lombok.Data;

/**
 * 枚举信息
 */
@Data
public class EnumInfo {

    public Class id;

    /**
     * 本来定义的字段类型
     */
    public Class define_clazz;
    /**
     * 所属枚举类
     */
    public Class enum_clazz;

    /**
     * 代表自身枚举实例的字段名称，默认是“name”
     * <br/>
     * 只在define_clazz为Enum.class时，才会根据“self_field”推断出“enum_clazz”
     */
    public String self_field="name";

    /**
     * 代表被拷贝枚举实例的字段名称，默认是“name”
     */
    public String from_field="name";

    /**
     * 代表要拷贝的枚举实例的字段名称，默认是“name”
     */
    public String to_field="name";

}
