package com.cyser.test.type;

import java.util.List;

public class Cat<V> {

    public Cat(){}

    public Cat(String name) {
        this.name = name;
    }

    public String name;

    public List<Color> color;

    public List<Color>[] colors;

    public List<V> values;

    public V v;
}
