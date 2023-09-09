package com.cyser.base.classloader;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class ByteBuddyClassLoader extends ClassLoader {

    @Getter
    @Setter
    private String basePackage = "com.cyser.base.bytebuddy";
    private String classPath;

    public ByteBuddyClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL url = classLoader.getResource("");
        this.classPath =url.getPath();
    }

    public ByteBuddyClassLoader(String classPath) {
        this.classPath = classPath;
    }

    public ByteBuddyClassLoader(String basePackage, String classPath, ClassLoader parent) {
        super(parent);
        this.basePackage = basePackage;
        this.classPath = classPath;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (isInBasePackage(name)) {
            try {
                byte[] classBytes = getClassBytes(name);
                return defineClass(name, classBytes, 0, classBytes.length);
            } catch (IOException e) {
                throw new ClassNotFoundException(name, e);
            }
        } else {
            return super.findClass(name);
        }
    }

    private boolean isInBasePackage(String className) {
        String packagePath = className.replace('.', File.separatorChar) + ".class";
        String basePackagePath = basePackage.replace('.', File.separatorChar);
        return packagePath.startsWith(basePackagePath);
    }

    private byte[] getClassBytes(String className) throws IOException {
        String classPath = this.classPath + File.separator + className.replace('.', File.separatorChar) + ".class";
        return Files.readAllBytes(new File(classPath).toPath());
    }
}