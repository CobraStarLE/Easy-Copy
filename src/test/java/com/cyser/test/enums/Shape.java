package com.cyser.test.enums;

import lombok.Getter;

public enum Shape {

    square(2,"正方形"),
    circle(5,"圆形"),
    triangle(8,"三角形");

    @Getter
    private int id;

    @Getter
    private String name;

    Shape(int id, String name){
        this.id=id;
        this.name=name;
    }
}
