package com.cyser.test;

import com.cyser.base.bean.FieldDefinition;
import com.cyser.base.utils.ClassUtil;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class FieldTest {
    public static void main(String[] args) throws ClassNotFoundException {
        List<Field> fields = FieldUtils.getAllFieldsList(UserExtend.class);
        List test_list = new ArrayList();
        test_list.add(1);
        UserExtend ue = new UserExtend();
        ue.haires = test_list;
        for (int i = 0; i < ue.haires.size(); i++) {
            System.out.println(ue.haires.get(i));
        }
        System.out.println();

        Class hair_clazz;
        System.out.println();
    }
}
