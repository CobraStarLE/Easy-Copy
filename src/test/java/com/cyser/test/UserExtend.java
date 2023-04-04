package com.cyser.test;

import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserExtend<K, V> extends User<K, V> {

    public UserExtend() {}

    public UserExtend(String id, String name) {
        super(id, name, 4);
    }

    public int age;
    public Integer player_num;

    public static void main(String[] args) {
        UserExtend extend = new UserExtend();
        extend.age = 18;

        List<Field> fields = FieldUtils.getAllFieldsList(UserExtend.class);
        List<Field> obj_fields = FieldUtils.getAllFieldsList(Cry.class);

        Map<String, Field> dest_fields_map = null;
        try {
            dest_fields_map =
                    fields.stream()
                          .collect(Collectors.toMap(Field::getName, f -> f, (name1, name2) -> name2));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        for (Map.Entry<String, Field> entry : dest_fields_map.entrySet()) {
            try {
                System.out.println(FieldUtils.readField(extend, entry.getKey()));
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
