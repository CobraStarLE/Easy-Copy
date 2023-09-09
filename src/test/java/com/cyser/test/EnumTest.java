package com.cyser.test;

import com.cyser.base.bean.TypeDefinition;
import com.cyser.base.enums.BaseEnum;
import com.cyser.base.utils.ClassUtil;
import com.cyser.test.enums.Fruit;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class EnumTest {

    public static void main(String[] args) throws IllegalAccessException {

        System.out.println(Integer.class.isAssignableFrom(int.class));
        System.out.println("------");
        System.out.println(BaseEnum.APPLE.name());
        System.out.println(BaseEnum.APPLE.toString());

        List<Field> fields= FieldUtils.getAllFieldsList(BaseEnum.class);
        List<Field> fields2= FieldUtils.getAllFieldsList(Fruit.class);
        Field field=FieldUtils.getField(BaseEnum.class,"name",true);
        Field fruit_field=FieldUtils.getField(Fruit.class,"color",true);
        List enum_list=EnumUtils.getEnumList(Fruit.class);
        Map enum_map=EnumUtils.getEnumMap(Fruit.class);
        for (Object obj:enum_list){
            System.out.println(FieldUtils.readField(fruit_field,obj,true));
        }
        System.out.println();
    }
}
