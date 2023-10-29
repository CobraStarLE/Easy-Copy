package com.cyser.test.common;

import com.cyser.base.annotations.EnumFormat;
import com.cyser.base.enums.CopyFeature;
import com.cyser.base.param.CopyParam;
import com.cyser.base.type.TypeReference;
import com.cyser.base.utils.BeanUtil;
import com.cyser.test.enums.Fruit;
import com.cyser.test.enums.Shape;
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
        dog.other=Fruit.pear.getNum();
        List<Kind> list=new ArrayList<>();
        list.add(new Kind("藏獒"));
        list.add(new Kind("田园犬"));
        dog.list=list;

        CopyParam cp=new CopyParam(CopyFeature.CASE_SENSITIVE,false);
        Cat cat= (Cat) BeanUtil.copy(null, dog, new TypeReference<Cat<Object>>() {},new TypeReference<Dog>() {},cp);
        System.out.println();
    }



    public static void main(String[] args) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Entity2Entity main=new Entity2Entity();
        main.testWithNoWrapper();
    }




    class Cat<T>{

        public Cat(){}
        private String NAME;

        private String color;

        public T kind;

        public Set<T> list;

        @EnumFormat(from_field = "num")
        public int fruit;

        @EnumFormat(from_field = "num",self_field = "name")
        public String other;
    }

    class Dog{
        public String name;

        public String color;

        public Kind kind;

        public List<Kind> list;

        @EnumFormat(from_field = "num",self_field = "name",to_field = "color")
        public int other;

    }

    class Kind{
        public Kind(){}

        public String type;

        public Kind(String type) {
            this.type=type;
        }
    }
}
