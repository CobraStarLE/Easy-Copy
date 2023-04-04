package com.cyser.test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class Student extends Person2<Integer, Boolean> {

    @SuppressWarnings("rawtypes")
    public static void main(String[] args) {

        Student student = new Student();
        Class clazz = student.getClass();
        System.out.println("获取父类对象：" + clazz.getSuperclass());
        /**
         * getGenericSuperclass()获得带有泛型的父类
         * Type是 Java 编程语言中所有类型的公共高级接口。它们包括原始类型、参数化类型、数组类型、类型变量和基本类型。
         */
        Type type = clazz.getGenericSuperclass();
        System.out.println(type);

        //ParameterizedType参数化类型，即泛型
        ParameterizedType p = (ParameterizedType) type;
        //getActualTypeArguments获取参数化类型的数组，泛型可能有多个
        Class c1 = (Class) p.getActualTypeArguments()[0];
        System.out.println(c1);
        Class c2 = (Class) p.getActualTypeArguments()[1];
        System.out.println(c2);
    }
}

class Person2<T, E> {}

/**
 * 运行结果：
 * 获取父类对象：class com.mycode.test.Person
 * com.mycode.test.Person<java.lang.Integer, java.lang.Boolean>
 * class java.lang.Integer
 * class java.lang.Boolean
 */