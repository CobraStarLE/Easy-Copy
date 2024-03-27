package com.cyser.test.type;

import com.cyser.base.enums.ClassTypeEnum;
import com.cyser.test.bean.Simple;

public class TypeTest {

    public static void main(String[] args) {
        ClassTypeEnum type_simple= ClassTypeEnum.valueOf(Simple.class);
        System.out.println();
    }
}
