package com.cyser.base.utils;

import com.cyser.base.FieldDefinition;
import com.cyser.base.annotations.TimeFormat;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.ClassUtils;

import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class ClassUtil {

    private ClassUtil() {
    }

    /**
     * 获取可序列化字段
     *
     * @param clazz
     * @return
     */
    public static List<Field> getSerializableFields(Class clazz) {
        List<Field> fields = Lists.newArrayList(getAllFieldsMap(clazz).values());//目标类所有字段
        return getSerializableFields(fields);
    }

    /**
     * 获取可序列化字段
     *
     * @param fields
     * @return
     */
    public static List<Field> getSerializableFields(Collection<Field> fields) {
        List<Field> list = new ArrayList<>();
        for (Field field : fields) {
            if (isSerializableField(field))
                list.add(field);
        }
        return list;
    }

    public static boolean isSerializableField(Field field) {
        boolean t1 = field.getAnnotation(Transient.class) != null;
        boolean t2 = Modifier.isTransient(field.getModifiers())
                || Modifier.isStatic(field.getModifiers())
                || Modifier.isFinal(field.getModifiers());

        return !t1 && !t2;
    }

    /**
     * 获取一个类所有的声明字段(包含超类)
     * <p><b>Note:</b>如果类中包含与超类相同名称的字段，则返回该类中字段
     *
     * @param clazz
     * @return
     */
    public static Collection<Field> getAllFieldsCollection(Class clazz) {
        Map<String, Field> map = new HashMap<>();
        Class<?> currentClass = clazz;
        while (currentClass != null) {
            final Field[] declaredFields = currentClass.getDeclaredFields();
            for (Field field : declaredFields) {
                if (!map.containsKey(field.getName()))
                    map.put(field.getName(), field);
            }
            currentClass = currentClass.getSuperclass();
        }
        return map.values();
    }

    /**
     * 获取一个类所有的声明字段(包含超类)
     * <p><b>Note:</b>如果类中包含与超类相同名称的字段，则返回该类中字段
     *
     * @param clazz
     * @return
     */
    public static Map<String, Field> getAllFieldsMap(Class clazz) {
        Map<String, Field> map = new HashMap<>();
        Class<?> currentClass = clazz;
        while (currentClass != null) {
            final Field[] declaredFields = currentClass.getDeclaredFields();
            for (Field field : declaredFields) {
                if (!map.containsKey(field.getName()))
                    map.put(field.getName(), field);
            }
            currentClass = currentClass.getSuperclass();
        }
        return map;
    }

    /**
     * 检查一个Class是否可以分配给另一个Class的变量
     *
     * @param cls
     * @param toClass
     * @param autoboxing
     * @return
     */
    public static boolean isAssignable(final Class<?> cls, final Class<?> toClass, final boolean autoboxing) {
        if (toClass == null) {
            return false;
        }
        if (cls == null) {
            return !toClass.isPrimitive();
        }
        Class<?> _cls = cls;
        Class<?> _toClass = toClass;
        if (_cls.isPrimitive()) {
            _cls = ClassUtils.primitiveToWrapper(_cls);
        }
        if (_toClass.isPrimitive()) {
            _toClass = ClassUtils.primitiveToWrapper(_toClass);
        }
        if (_cls.equals(_toClass)) {
            return true;
        }
        if (_cls.isAssignableFrom(String.class)
                && ClassUtils.isPrimitiveOrWrapper(_toClass)
                && (!(Void.TYPE.equals(_toClass) || Boolean.TYPE.equals(_toClass)))) {
            return true;
        }
        if (_toClass.isAssignableFrom(String.class)
                && ClassUtils.isPrimitiveOrWrapper(_cls)
                && (!(Void.TYPE.equals(_cls) || Boolean.TYPE.equals(_cls)))) {
            return true;
        }
        return ClassUtils.isAssignable(_cls, _toClass, autoboxing);
    }

    /**
     * 解析字段
     *
     * @param field
     * @return
     */
    public static FieldDefinition parseField(Field field) throws ClassNotFoundException {
        Type type = field.getType();
        Type generic_type = field.getGenericType();
        FieldDefinition fd = new FieldDefinition();
        if (!field.isAccessible())
            field.setAccessible(true);
        fd.field = field;
        fd.type = type;
        fd.genericType = generic_type;
        fd.raw_Type_class = ClassUtils.getClass(type.getTypeName());
        //如果是泛型
        if (generic_type instanceof ParameterizedType) {
            fd.isGeneric = true;
            Type[] param_types = ((ParameterizedType) generic_type).getActualTypeArguments();
            Class[] parameter_Type_classes = new Class[param_types.length];
            for (int i = 0; i < param_types.length; i++) {
                parameter_Type_classes[i] = ClassUtils.getClass(param_types[i].getTypeName());
            }
            fd.parameter_Type_classes = parameter_Type_classes;
        }
        //判断是否是基本类型
        fd.isPrimitive = fd.raw_Type_class.isPrimitive();
        //判断是否是基本封装类型
        fd.isPrimitiveWrapper = ClassUtils.isPrimitiveWrapper(fd.raw_Type_class);
        //判断是否是基本类型或者基本封装类型
        fd.isPrimitiveOrWrapper = fd.isPrimitive || fd.isPrimitiveWrapper;
        //判断是否是日期字段
        if (ClassUtils.isAssignable(fd.raw_Type_class, LocalDate.class)
                || ClassUtils.isAssignable(fd.raw_Type_class, LocalDateTime.class)
                || ClassUtils.isAssignable(fd.raw_Type_class, Date.class)
                || (ClassUtils.isAssignable(fd.raw_Type_class, String.class) && field.getAnnotation(TimeFormat.class) != null)) {
            fd.isTime = true;
            TimeFormat timeFormat = field.getAnnotation(TimeFormat.class);
            if (timeFormat != null)
                fd.timeFormat = timeFormat.value();
        }
        //判断是否是可序列化字段
        if (!isSerializableField(field))
            fd.isSerializable = false;
        return fd;
    }

}
