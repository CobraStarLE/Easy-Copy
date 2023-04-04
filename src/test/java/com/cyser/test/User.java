package com.cyser.test;

import java.util.Date;
import java.util.List;

public class User<K, V> implements Smile, Cry {

    public User() {}

    public User(String id, String name, Integer age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public String id;
    public String name;
    public Date birth;
    public int age;
    public List<String> haires;
    public List nn;

}
