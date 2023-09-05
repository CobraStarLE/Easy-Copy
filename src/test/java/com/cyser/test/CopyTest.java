package com.cyser.test;

import com.cyser.base.utils.BeanUtil;
import com.cyser.test.type.Cat;
import com.fasterxml.jackson.core.type.TypeReference;

import java.lang.reflect.InvocationTargetException;

public class CopyTest {

    private static void test1() throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Cat cat1=new Cat();
        Cat cat2=new Cat();
        BeanUtil.copy(cat2, cat1, new TypeReference<Cat<Object>>() {},new TypeReference<Cat<Object>>() {});
    }
    public static void main(String[] args) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        test1();
    }
}
