package com.cyser.test.type;

import com.cyser.base.param.CopyParam;
import com.cyser.base.type.TypeReference;
import com.cyser.base.utils.BeanUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CopyTest {

    public static void main(String[] args) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        /*List<Cat> list=new ArrayList<>();
        list.add(new Cat("黑莓"));
        list.add(new Cat("红莓"));
        list.add(new Cat("草莓"));
        Vector<Cat> vector=new Vector<>();

        CopyParam copyParam=new CopyParam();
        BeanUtil.copy(vector, list, new TypeReference<Vector<Cat>>() {}, new TypeReference<List<Cat>>() {},copyParam);
        System.out.println("~~~~~~~~~~");*/

        Set set=new HashSet();
        set.add(1);
        set.add(2);
        set.add(3);

        List<String> list1=new ArrayList<>();
        CopyParam copyParam=new CopyParam();
        BeanUtil.copy(list1, set, new TypeReference<List<String>>() {}, new TypeReference<Set>() {},copyParam);
        System.out.println("~~~~~~~~~~");


    }
}
