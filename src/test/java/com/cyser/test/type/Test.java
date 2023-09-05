package com.cyser.test.type;

import com.cyser.base.utils.ClassUtil;
import com.cyser.test.Boy;
import com.cyser.test.Sex;
import com.fasterxml.jackson.core.type.TypeReference;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Test {
    public static void main(String[] args) throws ClassNotFoundException {
//        Map<String, Field> catMap= ClassUtil.getAllFieldsMap(Cat.class);
//        FieldDefinition catFd=ClassUtil.parseField(catMap.get("values"));
//        System.out.println(catFd.type.getTypeName());


        System.out.println(Dog.class.getName());
        System.out.println(Dog.class.getSimpleName());

        System.out.println(Dog.class.hashCode());

        Cat<String> cat1=new Cat<>();
        Cat<Integer> cat2=new Cat<>();
        Dog dog=new Dog();
        Cat cat3=new Cat();
        List<String> list1=new ArrayList<>();
        List<Integer> list2=new ArrayList<>();
        List<String>[] list_arr=new ArrayList[2];
        String[] str_arr=new String[2];
        System.out.println("cat1:"+ClassUtil.isGenericClass(cat1.getClass()));
        System.out.println("cat2:"+ClassUtil.isGenericClass(cat2.getClass()));
        System.out.println("cat3:"+ClassUtil.isGenericClass(cat3.getClass()));
        System.out.println("dog:"+ClassUtil.isGenericClass(dog.getClass()));
        System.out.println("list1:"+ClassUtil.isGenericClass(list1.getClass()));
        System.out.println("list_arr:"+ClassUtil.isGenericClass(list_arr.getClass()));

        System.out.println(ClassUtil.isAssignable(list1.getClass(),list2.getClass(),true));

        TypeReference<Cat<String>> tf=new TypeReference<Cat<String>>() {};//Paramter
        TypeReference<Cat> tf2=new TypeReference<Cat>() {};//class
        TypeReference<Dog[]> tf3=new TypeReference<Dog[]>() {};//class
        TypeReference<Cat<String>[]> tf4=new TypeReference<Cat<String>[]>() {};//genericArrayType
        TypeReference<Cat<?>> tf5=new TypeReference<Cat<?>>() {};//paramter

        TypeReference<Dog> tf6=new TypeReference<Dog>() {};//class

//        ClassDefinition tf2_cf=ClassUtil.parseClass(tf2);

        if(tf.getType() instanceof ParameterizedType){
            ParameterizedType pt = (ParameterizedType) tf.getType();
            System.out.println(pt.getActualTypeArguments()[0].getTypeName());
        }


        Map<String, Field> cat2Map= ClassUtil.getAllFieldsMap(new Cat<String>(){}.getClass());

        System.out.println(Dog.class);

        Cat<Sex> cat=new Cat<Sex>(){};
        System.out.println(((ParameterizedType)cat.getClass().getGenericSuperclass()).getActualTypeArguments()[0].getTypeName());;

        System.out.println(Boy.class.getGenericSuperclass().getTypeName());
//        List<String> list=new ArrayList<>();
        System.out.println(((ParameterizedType)list1.getClass().getGenericSuperclass()).getActualTypeArguments()[0].getTypeName());
    }
}
