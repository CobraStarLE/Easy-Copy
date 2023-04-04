package com.cyser.test;

public class CloneTest {
    public static void main(String[] args) throws CloneNotSupportedException {
        Girl a = new Girl();
        Girl b = (Girl) a.clone();
        System.out.println(System.identityHashCode(a));
        System.out.println(System.identityHashCode(b));
    }
}
