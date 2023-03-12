package com.cyser.test.field;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CopyTest {

    public static void main(String[] args) {
        Girl girl = new Girl();
        girl.id = "1";
        girl.age = 18;
        girl.name=123L;
        girl.cells="123566";
        List<String> nn = new ArrayList<>();
        nn.add("红色");
        nn.add("绿色");
        girl.nn=nn;
        girl.birth=new Date();
        Boy boy=new Boy().copy(null, girl);
        System.out.println();
    }
}
