package com.cyser.test;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.lang.reflect.Field;
import java.util.Map;

public class HashBasedTableTest {

    public static void main(String[] args) {
       Table<Integer,String, String> FIELD_CACHE = HashBasedTable.create();
       FIELD_CACHE.put(1,"1","1");
        System.out.println(FIELD_CACHE.get(1,"1"));

    }
}
