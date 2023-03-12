package com.cyser.test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class StreamTest {

    public static void main(String[] args) {
        User u1 = new User("1", "小明", 12);
        User u2 = new User("2", "小红", 10);

        List<User> list = new ArrayList();
        list.add(u1);
        list.add(u2);
        int total = list.stream().flatMapToInt(o -> IntStream.of(o.age)).sum();
        System.out.println();
    }
}
