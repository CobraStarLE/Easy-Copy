package com.cyser.base.function;

/**
 * 五元函数
 * @param <A>
 * @param <B>
 * @param <C>
 * @param <D>
 * @param <E>
 * @param <R>
 */
@FunctionalInterface
public interface PentaFunction<A, B, C, D, E, R> {
    R apply(A a, B b, C c, D d, E e);
}