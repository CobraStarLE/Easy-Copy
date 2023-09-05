package com.cyser.test.type;

import com.cyser.base.bean.ClassDefinition;
import com.cyser.base.bean.TypeDefinition;
import com.cyser.base.cache.CopyableFieldsCache;
import com.cyser.base.utils.ClassUtil;
import com.fasterxml.jackson.core.type.TypeReference;

public class CopyableFieldsCacheTest {

    public static void main(String[] args) throws ClassNotFoundException {
        TypeReference tf=new TypeReference<Cat<Dog>[]>(){};
        TypeDefinition type_def= ClassUtil.parseType(tf.getType());
//        CopyableFieldsCache.getSerialFieldDefinitions(clazz_def);
    }
}
