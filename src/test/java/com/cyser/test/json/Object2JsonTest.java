package com.cyser.test.json;

import com.cyser.base.utils.JsonUtil;
import org.apache.commons.lang3.reflect.TypeUtils;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Object2JsonTest {

    public static void main(String[] args) {
        int a=18;
        System.out.println(JsonUtil.obj2Json(a));

        List<String>[] b=new ArrayList[2];
        List b1=new ArrayList();
        b[0]=b1;
        System.out.println(b.getClass());
        System.out.println(b.getClass().isArray());
        Type b_type=TypeUtils.getArrayComponentType(b.getClass());
        System.out.println(b_type.equals(GenericArrayType.class));
        System.out.println();
    }
}
