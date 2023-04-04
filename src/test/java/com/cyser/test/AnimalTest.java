package com.cyser.test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

class Head {
}

interface Animal<T> {
}

class Dog implements Animal<Head> {
}

public class AnimalTest {
    public static void main(String[] args) {
        Animal animal = new Dog();
        Type genericInterface = animal.getClass().getGenericInterfaces()[0];
        ParameterizedType genericInterface1 = (ParameterizedType) genericInterface;
        Class cname = (Class) genericInterface1.getActualTypeArguments()[0];
        System.out.println(cname);
        // 打印 class com.weblogc.design.test.Head
    }
}