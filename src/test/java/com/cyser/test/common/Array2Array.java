package com.cyser.test.common;

import com.cyser.base.type.TypeReference;
import com.cyser.base.utils.BeanUtil;
import com.cyser.test.Boy;
import com.cyser.test.User;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Array2Array {


    public static void main(String[] args) {
        List<User> users=new ArrayList<>();
        User user1=new User();
        user1.name="Lee Wrong";
        user1.age=18;
        User user2=new User();
        user2.name="过山峰";
        user2.age=18;

        users.add(user1);
        users.add(user2);

        try {
            Set<Boy> boys= (Set<Boy>) BeanUtil.copy(null, users, new TypeReference<Set<Boy>>() {},new TypeReference<List<User>>() {});
            System.out.println();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
