package com.cyser.test;

import com.esotericsoftware.kryo.Kryo;
import org.apache.commons.lang3.ClassUtils;
import org.springframework.core.ResolvableType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class KryoTest {

    public  static  void test1(){
        User user = new User();
        user.id="1";
        user.age=18;
        Kryo kryo=new Kryo();
        kryo.register(user.getClass());
        Object user2=kryo.copy(user);
        System.out.println();
    }

    public static void test2() throws ClassNotFoundException {
        List<User> list = new ArrayList<>(){};
        User user1 = new User();
        user1.id="1";
        user1.age=18;

        User user2 = new User();
        user2.id="1";
        user2.age=18;

        list.add(user1);
        list.add(user2);

        Object obj=list;

        Type ins=obj.getClass().getGenericInterfaces()[0];
        ParameterizedType parameterizedType=(ParameterizedType)ins;
        Class actualTypeArgument = (Class)parameterizedType.getActualTypeArguments()[0];
        System.out.println(actualTypeArgument);
        Type type=obj.getClass();
        if(type instanceof ParameterizedType){
            Type[] param_types = ((ParameterizedType) type).getActualTypeArguments();
            Class[] parameter_Type_classes = new Class[param_types.length];
            for (int i = 0; i < param_types.length; i++) {
                parameter_Type_classes[i] = ClassUtils.getClass(param_types[i].getTypeName());
            }
            System.out.println();
        }


        Kryo kryo=new Kryo();
        kryo.register(User.class);
        kryo.register(obj.getClass());
        Object list2=kryo.copy(list);
        System.out.println();
    }
    public static void test3() throws ClassNotFoundException {
        Map<String,String> map=new HashMap<>(){};
        Set<String> set=new HashSet<>(){};

        List<User> list = new ArrayList<>();
        User user1 = new User();
        user1.id="1";
        user1.age=18;

        User user2 = new User();
        user2.id="1";
        user2.age=18;

        list.add(user1);
        list.add(user2);

        Object obj=list;

        ResolvableType type=ResolvableType.forInstance(obj);
        ResolvableType super_type=type.getSuperType();
        Type r=super_type.getType();

        Kryo kryo=new Kryo();
        kryo.register(User.class);
        kryo.register(obj.getClass());
        Object list2=kryo.copy(list);
        System.out.println();
    }

    public static void main(String[] args) throws ClassNotFoundException {
//        test1();
//        test2();
        test3();
    }
}
