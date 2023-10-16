package com.cyser.base.utils;

import com.cyser.base.bean.FieldDefinition;
import com.cyser.base.bean.TypeDefinition;
import org.apache.commons.lang3.ObjectUtils;

public class DefinitionUtil {

    public TypeDefinition toDefinition(FieldDefinition field_df) throws ClassNotFoundException {
        TypeDefinition type_df=new TypeDefinition();
        if(ObjectUtils.allNotNull(field_df)){
            type_df.setParameter_type_Defines(field_df.parameter_type_Defines);
            type_df.setParameter_type_corresponds(field_df.parameter_type_corresponds);
            type_df.setComponetClassDefine(field_df.componetClassDefine);
            type_df.setUpperBounds(field_df.upperBounds);
            type_df.setLowerBounds(field_df.lowerBounds);
            type_df.setGenericComponentType(field_df.genericComponentType);
            type_df.setClass_type(field_df.class_type);
            type_df.setRaw_type(field_df.raw_type);
            type_df.setRaw_Type_class(field_df.raw_Type_class);
            type_df.setRuntime_class(field_df.runtime_class);
            type_df.setGeneric(field_df.isGeneric);
            type_df.setPrimitive(field_df.isPrimitive);
            type_df.setArray(field_df.isArray);
            type_df.setPrimitiveWrapper(field_df.isPrimitiveWrapper);
            type_df.setPrimitiveOrWrapper(field_df.isPrimitiveOrWrapper);
            type_df.setSerializable(field_df.isSerializable);
            type_df.setData_type(field_df.data_type);
        }
        return type_df;
    }
}
