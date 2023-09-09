package com.cyser.test;

import com.cyser.base.annotations.TimeFormat;
import com.cyser.base.utils.TimestampUtil;

import java.util.Date;
import java.util.List;

public class User<K, V> implements Smile, Cry {

    public User() {}

    public User(String id, String name, Integer age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    @TimeFormat(value = TimestampUtil.FastDateFormatPattern.CN_DATE_FORMAT)
    public String id;
    public String name;
    public Date birth;
    public int age;
    public List<String> haires;
    public List nn;

}
