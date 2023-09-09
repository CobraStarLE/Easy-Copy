package com.cyser.test.common;

import com.cyser.base.annotations.EnumFormat;
import com.cyser.base.utils.BeanUtil;
import com.cyser.test.enums.Fruit;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.annotations.VisibleForTesting;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Entity2Entity {

    /**
     * 没有范型
     */
    public void testWithNoWrapper() throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Dog dog=new Dog();
        dog.name="旺财";
        dog.color="黑色";
        dog.kind=new Kind("拉布拉多");
        dog.fruit=Fruit.pear;
        List<Kind> list=new ArrayList<>();
        list.add(new Kind("藏獒"));
        list.add(new Kind("田园犬"));
        dog.list=list;

        Cat cat= (Cat) BeanUtil.copy(null, dog, new TypeReference<Cat<Object>>() {},new TypeReference<Dog>() {});
        System.out.println();
    }



    public static void main(String[] args) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Entity2Entity main=new Entity2Entity();
        main.testWithNoWrapper();
    }




    class Cat<T>{

        public Cat(){}
        public String name;

        public String color;

        public T kind;

        public Set<T> list;

        @EnumFormat(from_field = "num")
        public int fruit;
    }

    class Dog{
        public String name;

        public String color;

        public Kind kind;

        public List<Kind> list;

        public Fruit fruit;

    }

    class Kind{
        public Kind(){}

        public String type;

        public Kind(String type) {
            this.type=type;
        }
    }
}
