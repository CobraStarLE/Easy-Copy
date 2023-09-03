package com.cyser.test;

import com.cyser.base.annotations.EnumFormat;
import com.cyser.base.enums.BaseEnum;
import lombok.Data;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Data
public class Boy extends Sex {

    @EnumFormat(enum_class= BaseEnum.class,field_from="",field_local = "")
    private String name;

    public <E extends Boy> void test1(E e) {
//        System.out.println(e.getName());
    }

    public <E extends Boy> void test2(List<E> list) {
        List<E> list2 = new ArrayList<>();
//        list2.get(0).getName();
    }


    public static void main(String[] args) {
        List<? extends Sex> list = new ArrayList<>();

        Type type = list.getClass().getGenericSuperclass();
        ParameterizedType pt = (ParameterizedType) type;
        System.out.println(pt.getActualTypeArguments()[0].toString());
    }
}


