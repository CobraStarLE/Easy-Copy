package com.cyser.base.bean;

import com.cyser.base.enums.DataTypeEnum;
import lombok.Data;

import java.util.Map;
@Data
public class TypeDefinition extends CopyDefinition{

    /**
     * 当为ParameterizedType
     */
    public TypeDefinition[] parameter_type_Defines;

    /**
     * 当isGeneric为true，并且类型是Class或者ParameterizedType时
     */
    public Map<String,Class> parameter_type_corresponds;//类上范型真实对应，比如我用定义的类Cat<T> ,然后真实使用是new Cat<Color>,这里存的就是T->Color.class

    /**
     * 当为Class时，并且是数组时，数组类型的类定义
     */
    public TypeDefinition componetClassDefine;

    /**
     * 当为WildcardType时
     */
    public TypeDefinition upperBounds;
    public TypeDefinition lowerBounds;

    /**
     * 当为GenericArrayType时
     */
    public TypeDefinition genericComponentType;

    public DataTypeEnum getData_type(){
        return data_type;
    }

}
