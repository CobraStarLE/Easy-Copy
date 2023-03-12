package com.cyser.test;

import com.cyser.base.function.TernaryFunction;

public class TernaryFunctionTest {

    public static void main(String[] args) {
        TernaryFunction<String, String,Integer,String> te =(x,y,z)->x+y+(" "+z);
        String s =te.apply("y", "qj",2065);
        System.out.println(s);
    }
}
