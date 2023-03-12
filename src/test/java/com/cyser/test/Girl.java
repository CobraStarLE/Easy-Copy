package com.cyser.test;

public class Girl extends Sex implements Cloneable {

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
