package com.cyser.test.classloader;

import java.net.URL;

public class Test {

    public static void main(String[] args) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL url = classLoader.getResource("");
        String classPath = url.getPath();
        System.out.println(classPath);
    }
}
