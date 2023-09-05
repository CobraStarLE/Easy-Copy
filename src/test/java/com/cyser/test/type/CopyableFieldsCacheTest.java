package com.cyser.test.type;

import com.cyser.base.bean.TypeDefinition;
import com.cyser.base.cache.CopyableFieldsCache;
import com.cyser.base.utils.ClassUtil;
import com.fasterxml.jackson.core.type.TypeReference;

public class CopyableFieldsCacheTest {

    public static void main(String[] args) throws ClassNotFoundException {
        TypeReference tf1=new TypeReference<Cat<Dog>>(){};
        TypeReference tf2=new TypeReference<Cat<Color>>(){};
        TypeReference tf3=new TypeReference<Cat>(){};
        TypeDefinition type_def1= ClassUtil.parseType(tf1.getType());
        TypeDefinition type_def2= ClassUtil.parseType(tf2.getType());
        TypeDefinition type_def3= ClassUtil.parseType(tf3.getType());
//        CopyableFieldsCache.getSerialFieldDefinitions(type_def1);
        CopyableFieldsCache.getSerialFieldDefinitions(type_def3);
    }
}
