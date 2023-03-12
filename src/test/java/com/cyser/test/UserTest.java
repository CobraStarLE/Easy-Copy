package com.cyser.test;

public class UserTest {

    public static void main(String[] args) {
        User u1 = new User("1","小明",12);
        User u2 = new User("2","小红",10);
        User u3=u2;
        u2=u1;
        System.out.println(u3.id);
    }
}
