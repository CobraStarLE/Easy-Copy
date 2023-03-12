package com.cyser.test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

class Money{
}
class Person<T>{
}
class Man extends Person<Money>{
}
public class ManTest {
    public static void main(String[] args){
        Man man=new Man();
        Type _super=man.getClass().getGenericSuperclass();
        ParameterizedType parameterizedType=(ParameterizedType)(man.getClass().getGenericSuperclass());
        Class actualTypeArgument = (Class)parameterizedType.getActualTypeArguments()[0];
        System.out.println(actualTypeArgument);

    }
}