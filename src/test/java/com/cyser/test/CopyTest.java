package com.cyser.test;

import com.cyser.base.type.TypeReference;
import com.cyser.base.utils.BeanUtil;
import com.cyser.test.type.Cat;

import java.lang.reflect.InvocationTargetException;

public class CopyTest {

    private void test1() throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Cat cat1=new Cat();
        Boy cat2=new Boy();

        cat1.name="小强";
        BeanUtil.copy(cat2, cat1, new TypeReference<Boy>() {},new TypeReference<Cat<Object>>() {});
        System.out.println();
    }
    public static void main(String[] args) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        CopyTest copyTest=new CopyTest();
        copyTest.test1();
    }
}
