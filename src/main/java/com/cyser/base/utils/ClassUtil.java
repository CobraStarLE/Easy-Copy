package com.cyser.base.utils;

import com.cyser.base.ClassDefinition;
import com.cyser.base.FieldDefinition;
import com.cyser.base.TypeDefinition;
import com.cyser.base.annotations.TimeFormat;
import com.cyser.base.enums.TypeEnum;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.reflect.TypeUtils;

import java.beans.Transient;
import java.lang.reflect.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class ClassUtil {

    private ClassUtil() {}

    /**
     * 判断一个类是否有范型,例如<br/>
     * <pre>
     * {@code
     * List<String>、List<String>[]
     * }
     * </pre>
     * 都算作有范型
     * @param clazz
     * @return true:是
     */
    public static boolean isGenericClass(Class<?> clazz) {
        if(clazz.isArray()){
           return isGenericClass(clazz.getComponentType());
        }
        // 获取类的类型参数
        TypeVariable<? extends Class<?>>[] typeParameters = clazz.getTypeParameters();

        // 如果类的类型参数不为空，则认为是泛型类
        return typeParameters.length > 0;
    }

    /**
     * 获取可序列化字段
     *
     * @param clazz
     * @return
     */
    public static List<Field> getSerializableFields(Class clazz) {
        List<Field> fields = Lists.newArrayList(getAllFieldsMap(clazz).values()); // 目标类所有字段
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
            if (isSerializableField(field)) list.add(field);
        }
        return list;
    }

    /**
     * 判断类是否可序列化
     *
     * @param clazz
     * @return true:可序列化 <br>
     * false:不可序列化
     */
    public static boolean isSerializableClass(Class clazz) {
        boolean t =
                Modifier.isTransient(clazz.getModifiers())
                        || Modifier.isStatic(clazz.getModifiers())
                        || Modifier.isFinal(clazz.getModifiers());

        return !t;
    }

    /**
     * 判断字段是否可序列化
     *
     * @param field
     * @return true:可序列化 <br>
     * false:不可序列化
     */
    public static boolean isSerializableField(Field field) {
        boolean t1 = field.getAnnotation(Transient.class) != null;
        boolean t2 =
                Modifier.isTransient(field.getModifiers())
                || Modifier.isStatic(field.getModifiers())
                || Modifier.isFinal(field.getModifiers());

        return !t1 && !t2;
    }



    /**
     * 获取一个类所有的声明字段(包含超类)
     *
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
                if (!map.containsKey(field.getName())) map.put(field.getName(), field);
            }
            currentClass = currentClass.getSuperclass();
        }
        return map.values();
    }

    /**
     * 获取一个类所有的声明字段(包含超类)
     *
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
                if (!map.containsKey(field.getName())) map.put(field.getName(), field);
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
     * @return true:可分配 <br>
     * false:不可分配
     */
    public static boolean isAssignable(
            final Class<?> cls, final Class<?> toClass, final boolean autoboxing) {
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
        if(isCollectionOrMapOrArray(_cls)&&isCollectionOrMapOrArray(_toClass)){
            //在这里并不是真正的判断可以分配变量，真正可以分配变量需要判断范型类型是否可以
            return true;
        }

        return ClassUtils.isAssignable(_cls, _toClass, autoboxing);
    }

    /**
     * 判断Class是否是集合或者Map或者数组
     * @return
     */
    public static boolean isCollectionOrMapOrArray(Class clazz){
       return isCollection(clazz)||isMap(clazz)||isArray(clazz);
    }

    /**
     * 判断Class是否是集合
     * @return
     */
    public static boolean isCollection(Class clazz){
        if(Collection.class.isAssignableFrom(clazz)){
            return true;
        }
        return false;
    }

    /**
     * 判断Class是否是Map
     * @return
     */
    public static boolean isMap(Class clazz){

        if(Map.class.isAssignableFrom(clazz)){
            return true;
        }

        return false;
    }

    /**
     * 判断Class是否是数组
     * @return
     */
    public static boolean isArray(Class clazz){
        if(clazz.isArray()){
            return true;
        }
        return false;
    }

    public static TypeDefinition parseType(Type type) throws ClassNotFoundException {
        TypeDefinition td=new TypeDefinition();
        td.rawType=type;
        td.type=TypeEnum.valueOf(type);
        td.rawClass=type.getClass();
        td.isGeneric=true;
        if(td.type==TypeEnum.Unknown){
            throw new RuntimeException("未识别的类型: "+((type == null) ? "[null]" : type.toString()));
        }
        if(td.type==TypeEnum.ParameterizedType){
            ParameterizedType parameter_type= (ParameterizedType) type;
            td.rawClass=Class.forName(parameter_type.getRawType().getTypeName());
            Type[] actualTypeArguments=parameter_type.getActualTypeArguments();
            TypeDefinition[] parameter_type_Defines=new TypeDefinition[actualTypeArguments.length];
            for (int i = 0; i < actualTypeArguments.length; i++) {
                parameter_type_Defines[i]=parseType(actualTypeArguments[i]);
            }
            td.parameter_type_Defines=parameter_type_Defines;
        }else if(td.type==TypeEnum.Class){
            td.isGeneric=false;
            Class clazz= (Class) type;
            //判断是否是数组
            if(clazz.getComponentType()!=null){
                td.isArray=true;
                ClassDefinition componetClassDefine=parseClass(clazz.getComponentType());
                td.componetClassDefine=componetClassDefine;
            }else{
                td.rawClass=Class.forName(td.rawType.getTypeName());
                td.isSerializable=isSerializableClass(td.rawClass);
                // 判断是否是基本类型
                td.isPrimitive = td.rawClass.isPrimitive();
                // 判断是否是基本封装类型
                td.isPrimitiveWrapper = ClassUtils.isPrimitiveWrapper(td.rawClass);
                // 判断是否是基本类型或者基本封装类型
                td.isPrimitiveOrWrapper = td.isPrimitive || td.isPrimitiveWrapper;
            }
        }else if(td.type==TypeEnum.GenericArrayType){
            td.isArray=true;
            GenericArrayType genricArrayType= (GenericArrayType) type;
            Type genericComponentType=genricArrayType.getGenericComponentType();
            td.genericComponentType=parseType(genericComponentType);
        }else if(td.type==TypeEnum.WildcardType){
            WildcardType wildcardType= (WildcardType) type;
            Type[] upperBounds=wildcardType.getUpperBounds();
            if(ObjectUtils.isNotEmpty(upperBounds)){
                td.upperBounds=parseClass((Class) upperBounds[0]);
            }
            Type[] lowerBounds=wildcardType.getLowerBounds();
            if(ObjectUtils.isNotEmpty(lowerBounds)){
                td.lowerBounds=parseClass((Class) lowerBounds[0]);
            }
        }else if(td.type==TypeEnum.TypeVariable){

        }
        return td;
    }

    public static ClassDefinition parseClass(Class clazz){
        ClassDefinition cf=new ClassDefinition();
        if(isGenericClass(clazz)){
            throw new RuntimeException("不支持解析带有范型类型的类["+clazz.getName()+"]！");
        }
        cf.isGeneric=false;
        cf.clazz=clazz;
        cf.rawType= clazz;
        cf.type=TypeEnum.Class;
        //判断是否是数组
        if(clazz.isArray()){
            cf.isArray=true;
            ClassDefinition componetClassDefine=parseClass(clazz.getComponentType());
            cf.componentClassDefine=componetClassDefine;
        }
        cf.isSerializable=isSerializableClass(clazz);
        // 判断是否是基本类型
        cf.isPrimitive = clazz.isPrimitive();
        // 判断是否是基本封装类型
        cf.isPrimitiveWrapper = ClassUtils.isPrimitiveWrapper(clazz);
        // 判断是否是基本类型或者基本封装类型
        cf.isPrimitiveOrWrapper = cf.isPrimitive || cf.isPrimitiveWrapper;

        return cf;
    }

    public static ClassDefinition parseClass(TypeReference tr) throws ClassNotFoundException {
        ClassDefinition cf=new ClassDefinition();
        Type type=tr.getType();
        cf.rawType=type;
        cf.type=TypeEnum.valueOf(type);
        if(cf.type==TypeEnum.Unknown){
            throw new RuntimeException("未识别的类型: "+((type == null) ? "[null]" : type.toString()));
        }
        cf.isGeneric=isGenericClass((Class<?>) cf.rawType);
        if((!cf.isGeneric)&&cf.type==TypeEnum.Class){
            return parseClass((Class) cf.rawType);
        }
        if(cf.isGeneric){
            if(cf.type==TypeEnum.ParameterizedType){
                ParameterizedType parameter_type= (ParameterizedType) type;
                cf.parameterType=parseType(parameter_type);

            }else if(cf.type==TypeEnum.GenericArrayType){
                GenericArrayType genericArrayType= (GenericArrayType) type;
                cf.genericComponentType=parseType(genericArrayType);
            }
        }
        return cf;
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
        if (!field.isAccessible()) field.setAccessible(true);
        fd.field = field;
        fd.type = type;
        fd.genericType = generic_type;
        fd.raw_Type_class = ClassUtils.getClass(type.getTypeName());
        // 如果是泛型
        if (generic_type instanceof ParameterizedType) {
            fd.isGeneric = true;
            Type[] param_types = ((ParameterizedType) generic_type).getActualTypeArguments();
            Class[] parameter_Type_classes = new Class[param_types.length];
            for (int i = 0; i < param_types.length; i++) {
                if(param_types[i] instanceof TypeVariable){
                    System.out.println(TypeUtils.getRawType(param_types[i],generic_type));
                    System.out.println();
                }
                parameter_Type_classes[i] = ClassUtils.getClass(param_types[i].getTypeName());
            }
            fd.parameter_Type_classes = parameter_Type_classes;
        }
        // 判断是否是基本类型
        fd.isPrimitive = fd.raw_Type_class.isPrimitive();
        // 判断是否是基本封装类型
        fd.isPrimitiveWrapper = ClassUtils.isPrimitiveWrapper(fd.raw_Type_class);
        // 判断是否是基本类型或者基本封装类型
        fd.isPrimitiveOrWrapper = fd.isPrimitive || fd.isPrimitiveWrapper;
        // 判断是否是日期字段
        if (ClassUtils.isAssignable(fd.raw_Type_class, LocalDate.class)
            || ClassUtils.isAssignable(fd.raw_Type_class, LocalDateTime.class)
            || ClassUtils.isAssignable(fd.raw_Type_class, Date.class)
            || (ClassUtils.isAssignable(fd.raw_Type_class, String.class)
                && field.getAnnotation(TimeFormat.class) != null)) {
            fd.isTime = true;
            TimeFormat timeFormat = field.getAnnotation(TimeFormat.class);
            if (timeFormat != null) fd.timeFormat = timeFormat.value();
        }
        // 判断是否是可序列化字段
        if (!isSerializableField(field)) fd.isSerializable = false;
        return fd;
    }
}
