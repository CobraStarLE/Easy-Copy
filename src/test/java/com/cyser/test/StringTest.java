package com.cyser.test;

public class StringTest {
    public static void main(String[] args) {
        String a = "hello";
        System.out.println(System.identityHashCode(a));
        String _a = a.intern();
        System.out.println(System.identityHashCode(a));
        System.out.println(System.identityHashCode(_a));
        String b = "hello";
        System.out.println(System.identityHashCode(b));
        System.out.println("===========");
        String str2 = "SEUCalvin";
        System.out.println(System.identityHashCode(str2));
        String str1 = "SEU" + "Calvin";
        System.out.println(System.identityHashCode(str1));
        System.out.println(System.identityHashCode(str1.intern()));
    }
}
/// 输出：
///        2060468723
///        2060468723
///        622488023
///        622488023
