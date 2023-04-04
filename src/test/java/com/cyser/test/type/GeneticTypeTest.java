package com.cyser.test.type;

import java.lang.reflect.*;
import java.util.List;

public class GeneticTypeTest<T> {

    // 这里面有各种各样的数组：各有不同 方便看测试效果
    // 含有泛型数组的才是GenericArrayType
    public void testGenericArrayType(
            List<String>[] pTypeArray,
            T[] vTypeArray,
            List<String> list,
            List<T> typeVariableList,
            List<? extends Number> wildcardList,
            String[] strings,
            GeneticTypeTest[] test) {}

    public static void main(String[] args) {
        Method[] declaredMethods = GeneticTypeTest.class.getDeclaredMethods();

        for (Method method : declaredMethods) {
            // main方法不用处理
            if (method.getName().startsWith("main")) {
                continue;
            }

            // 开始处理该方法===打印出此方法签名
            System.out.println(
                    "declare Method:"
                    + method); // declare Method:public void
            // com.fsx.maintest.GenericArrayTypeTest.testGenericArrayType(java.util.List[],java.lang.Object[],java.util.List,java.lang.String[],com.fsx.maintest.GenericArrayTypeTest[])

            // 该方法能获取到该方法所有的实际的参数化类型，比如本例中有五个参数，那数组长度就是5
            Type[] types = method.getGenericParameterTypes();

            // 分组打印出来
            for (Type type : types) {

                if (type instanceof ParameterizedType parameterizedType) {
                    System.out.println("**************");
                    System.out.println(
                            "ParameterizedType type [list typeVariableList wildcardList]: " + parameterizedType);
                    System.out.println("ParameterizedType's rawType: " + parameterizedType.getRawType());
                    System.out.println(
                            "ParameterizedType's ownerType is " + (parameterizedType.getOwnerType()));
                    System.out.println(
                            "ParameterizedType's actualType is  "
                            + parameterizedType.getActualTypeArguments()[0]);
                    System.out.println(
                            "ParameterizedType's actualType is TypeVariable "
                            + (parameterizedType.getActualTypeArguments()[0] instanceof TypeVariable));
                    System.out.println(
                            "ParameterizedType's actualType is WildcardType "
                            + (parameterizedType.getActualTypeArguments()[0] instanceof WildcardType));
                    System.out.println(
                            "ParameterizedType's actualType is Class "
                            + (parameterizedType.getActualTypeArguments()[0] instanceof Class));
                    System.out.println("**************");
                }
                if (type instanceof GenericArrayType genericArrayType) {
                    System.out.println("---------------");
                    System.out.println("GenericArrayType type [pTypeArray vTypeArray]: " + genericArrayType);
                    Type genericComponentType = genericArrayType.getGenericComponentType();
                    System.out.println(
                            "genericComponentType [pTypeArray vTypeArray's component Type]:"
                            + genericComponentType);
                    System.out.println("---------------");
                }
                if (type instanceof WildcardType wildcardType) {
                    System.out.println("WildcardType type [wildcardList]: " + wildcardType);
                }
                if (type instanceof TypeVariable typeVariable) {
                    System.out.println("TypeVariable type [typeVariable]:" + typeVariable);
                }
                if (type instanceof Class clazz) {
                    System.out.println("type [strings test]: " + clazz);
                }
            }
        }
    }
}
