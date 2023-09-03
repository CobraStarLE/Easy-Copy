package com.cyser.test.type;

import com.cyser.base.param.CopyParam;
import com.cyser.base.utils.BeanUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import org.checkerframework.checker.units.qual.C;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class CopyTest {

    public static void main(String[] args) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        List<Cat> list=new ArrayList<>();
        list.add(new Cat("黑莓"));
        list.add(new Cat("红莓"));
        list.add(new Cat("草莓"));
        Vector<Cat> vector=new Vector<>();

        CopyParam copyParam=new CopyParam();
        BeanUtil.copy(vector, list, new TypeReference<Vector<Cat>>() {}, new TypeReference<List<Cat>>() {},copyParam);
        System.out.println();

    }
}
