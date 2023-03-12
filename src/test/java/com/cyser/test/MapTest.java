package com.cyser.test;

import com.cyser.base.FieldDefinition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MapTest {
    public static void main(String[] args) {
        Map<String, FieldDefinition> maps=new HashMap<>();
        System.out.println(maps.values()==null);
        System.out.println(maps.values().size());
        List< FieldDefinition> serial_src_fd_list = maps.values().stream().filter(o -> o.isSerializable).collect(Collectors.toList());

        System.out.println(serial_src_fd_list.size());
    }
}
