package com.cyser.base.function;

/**
 * 四元函数
 * @param <A>
 * @param <B>
 * @param <C>
 * @param <D>
 * @param <R>
 */
@FunctionalInterface
public interface QuadFunction<A, B, C, D, R> {
    R apply(A a, B b, C c, D d);
}