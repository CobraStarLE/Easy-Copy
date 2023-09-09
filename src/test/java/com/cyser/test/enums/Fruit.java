package com.cyser.test.enums;

import lombok.Getter;
import lombok.Setter;

public enum Fruit {

    apple(2,"red"),pear(5,"green");

    @Getter
    @Setter
    private int num;

    @Getter
    @Setter
    private String color;

    Fruit(int num,String color){
        this.num=num;
        this.color=color;
    }
}
