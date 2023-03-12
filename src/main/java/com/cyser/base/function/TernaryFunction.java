package com.cyser.base.function;
import java.util.function.BiFunction;
 
/**
 * 三元函数
 * 
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public interface TernaryFunction<T,U,V,R>{
    R apply(T t,U u,V v);
    
    default public  BiFunction<T,U,R> get(V v){
        return (t,u)->this.apply(t,u,v);
    }
}