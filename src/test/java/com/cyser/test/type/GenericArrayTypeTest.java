package com.cyser.test.type;

import com.cyser.base.bean.FieldDefinition;
import com.cyser.base.utils.ClassUtil;
import org.apache.commons.lang3.reflect.TypeUtils;

import java.lang.reflect.Field;
import java.util.Map;

public class GenericArrayTypeTest {

    public static void main(String[] args) throws ClassNotFoundException {
       Map<String, Field> catMap=ClassUtil.getAllFieldsMap(Cat.class);
       Map<String, Field> dogMap=ClassUtil.getAllFieldsMap(Dog.class);
        FieldDefinition catFd=ClassUtil.parseField(catMap.get("colors"));
        FieldDefinition dogFd=ClassUtil.parseField(dogMap.get("colors"));
//        System.out.println(TypeUtils.);
        System.out.println(TypeUtils.isAssignable(catFd.type,dogFd.type));
        System.out.println(TypeUtils.isAssignable(dogFd.type,catFd.type));
    }
}
