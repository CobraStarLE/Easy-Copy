package com.cyser.base.utils;

import com.cyser.base.annotations.EnumFormat;
import com.cyser.base.annotations.TimeFormat;
import com.cyser.base.annotations.Timemode;
import com.cyser.base.bean.EnumInfo;
import com.cyser.base.bean.FieldDefinition;
import com.cyser.base.bean.TypeDefinition;
import com.cyser.base.classloader.ByteBuddyClassLoader;
import com.cyser.base.enums.ClassTypeEnum;
import com.cyser.base.enums.DataTypeEnum;
import com.cyser.base.enums.TimeMode;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.*;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.AnnotationUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;

import java.beans.Transient;
import java.lang.reflect.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class ClassUtil {

    private ClassUtil() {
    }

    public static final Integer DEFAULT_HASHCODE=-999999999;

    private static Table<Integer,String,Map<String,Field>> FIELD_CACHE = HashBasedTable.create();

    /**
     * 判断一个类是否有范型,例如<br/>
     * <pre>
     * {@code
     * List<String>、List<String>[]
     * }
     * </pre>
     * 都算作有范型
     *
     * @param clazz
     * @return true:是
     */
    public static boolean isGenericClass(Class<?> clazz) {
        if (clazz.isArray()) {
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
    public static List<Field> getSerializableFields(ClassLoader classLoader,Class clazz) throws ClassNotFoundException {
        List<Field> fields = Lists.newArrayList(getAllFieldsMap(classLoader,clazz).values()); // 目标类所有字段
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
    public static Collection<Field> getAllFieldsCollection(ClassLoader classLoader,Class clazz) throws ClassNotFoundException {
        Map<String, Field> map = getAllFieldsMap(classLoader,clazz);
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
    public static synchronized Map<String, Field> getAllFieldsMap(ClassLoader classLoader,Class clazz) throws ClassNotFoundException {
        String cache_key=clazz.getName();
        ClassLoader curr_classLoader=clazz.getClassLoader();
        if(classLoader!=null&&curr_classLoader!=null&&classLoader!=clazz.getClassLoader()){
            clazz=classLoader.loadClass(clazz.getName());
        }
        Integer hashCode=classLoader==null?DEFAULT_HASHCODE:classLoader.hashCode();
        Map<String, Field> map = FIELD_CACHE.get(hashCode,cache_key);
        if (map == null) {
            synchronized (ClassUtil.class){
                map = FIELD_CACHE.get(classLoader,cache_key);
                if(map==null){
                    map=new HashMap<>();
                    Class<?> currentClass = clazz;
                    while (currentClass != null) {
                        final Field[] declaredFields = currentClass.getDeclaredFields();
                        for (Field field : declaredFields) {
                            if (!map.containsKey(field.getName())) map.put(field.getName(), field);
                        }
                        currentClass = currentClass.getSuperclass();
                    }
                    FIELD_CACHE.put(hashCode,cache_key,map);
                }
            }
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
        if (isCollectionOrMapOrArray(_cls) && isCollectionOrMapOrArray(_toClass)) {
            //在这里并不是真正的判断可以分配变量，真正可以分配变量需要判断范型类型是否可以
            return true;
        }

        return ClassUtils.isAssignable(_cls, _toClass, autoboxing);
    }

    /**
     * 判断Class是否是集合或者Map或者数组
     *
     * @return
     */
    public static boolean isCollectionOrMapOrArray(Class clazz) {
        return isCollection(clazz) || isMap(clazz) || isArray(clazz);
    }

    /**
     * 判断Class是否是集合
     *
     * @return
     */
    public static boolean isCollection(Class clazz) {
        return Collection.class.isAssignableFrom(clazz);
    }

    /**
     * 判断Class是否是Map
     *
     * @return
     */
    public static boolean isMap(Class clazz) {

        return Map.class.isAssignableFrom(clazz);
    }

    /**
     * 判断Class是否是数组
     *
     * @return
     */
    public static boolean isArray(Class clazz) {
        return clazz.isArray();
    }

    public static TypeDefinition parseType(Type type) throws ClassNotFoundException {
        TypeDefinition td = new TypeDefinition();
        td.raw_type = type;
        td.class_type = ClassTypeEnum.valueOf(type);
        td.raw_Type_class = type.getClass();
        td.runtime_class=td.raw_Type_class;
        if (td.class_type == ClassTypeEnum.Unknown) {
            throw new RuntimeException("未识别的类型: " + ((type == null) ? "[null]" : type.toString()));
        }
        if (td.class_type == ClassTypeEnum.ParameterizedType) {
            td.isGeneric = true;
            ParameterizedType parameter_type = (ParameterizedType) type;
            td.runtime_class = Class.forName(parameter_type.getRawType().getTypeName());
            Type[] actualTypeArguments = parameter_type.getActualTypeArguments();
            TypeDefinition[] parameter_type_Defines = new TypeDefinition[actualTypeArguments.length];
            TypeVariable[] typeVariables= td.runtime_class.getTypeParameters();
            Map<String,Class> parameter_type_corresponds=new HashMap<>();
            for (int i = 0; i < actualTypeArguments.length; i++) {
                parameter_type_Defines[i] = parseType(actualTypeArguments[i]);
                parameter_type_corresponds.put(typeVariables[i].getName(),parameter_type_Defines[i].runtime_class);
            }
            td.parameter_type_corresponds=parameter_type_corresponds;
            td.parameter_type_Defines = parameter_type_Defines;
        } else if (td.class_type == ClassTypeEnum.Class) {
            Class clazz = (Class) type;
            td.isGeneric = isGenericClass(clazz);

            //判断是否是数组
            if (clazz.getComponentType() != null) {
                td.isArray = true;
                TypeDefinition componetClassDefine = parseType(clazz.getComponentType());
                td.componetClassDefine = componetClassDefine;
            } else {
                td.runtime_class = Class.forName(td.raw_type.getTypeName());
                td.isSerializable = isSerializableClass(td.runtime_class);
                // 判断是否是基本类型
                td.isPrimitive = td.runtime_class.isPrimitive();
                // 判断是否是基本封装类型
                td.isPrimitiveWrapper = ClassUtils.isPrimitiveWrapper(td.runtime_class);
                // 判断是否是基本类型或者基本封装类型
                td.isPrimitiveOrWrapper = td.isPrimitive || td.isPrimitiveWrapper;
                Map<String,Class> parameter_type_corresponds=new HashMap<>();
                if(td.isGeneric){//如果是范型
                    TypeVariable<? extends Class<?>>[] typeVariables = clazz.getTypeParameters();
                    for (int i = 0; i < typeVariables.length; i++) {
                        parameter_type_corresponds.put(typeVariables[i].getName(),Object.class);
                    }
                }
                td.parameter_type_corresponds=parameter_type_corresponds;
            }
        } else if (td.class_type == ClassTypeEnum.GenericArrayType) {
            td.isGeneric = true;
            td.isArray = true;
            GenericArrayType genricArrayType = (GenericArrayType) type;
            Type genericComponentType = genricArrayType.getGenericComponentType();
            td.genericComponentType = parseType(genericComponentType);
        } else if (td.class_type == ClassTypeEnum.WildcardType) {
            td.isGeneric = true;
            WildcardType wildcardType = (WildcardType) type;
            Type[] upperBounds = wildcardType.getUpperBounds();
            if (ObjectUtils.isNotEmpty(upperBounds)) {
                td.upperBounds = parseType((Class) upperBounds[0]);
            }
            Type[] lowerBounds = wildcardType.getLowerBounds();
            if (ObjectUtils.isNotEmpty(lowerBounds)) {
                td.lowerBounds = parseType((Class) lowerBounds[0]);
            }
        } else if (td.class_type == ClassTypeEnum.TypeVariable) {
            td.isGeneric = false;
        }
        td.setData_type(DataTypeEnum.valueOf(td.runtime_class));
        return td;
    }

    /**
     * 假如一个类中字段定义为List&lt;T&gt; list,T在运行中类型为String.class,我们对该字段生成
     * <br/>
     * 一个临时的新字段List&lt;String&gt; list
     * @param field_def
     * @return
     */
    public static Type generateRuntimeField(FieldDefinition field_def) {
        Field field=field_def.field;
        Class declaredClazz=field.getDeclaringClass();
        if(field_def.isGeneric){
            String raw_class_name=declaredClazz.getName();
            ByteBuddyClassLoader classLoader=new ByteBuddyClassLoader();
            int index=raw_class_name.lastIndexOf("$");
            String tmpClassName=raw_class_name+Thread.currentThread().getId();
            if(index>0){
                tmpClassName=raw_class_name.substring(+1)+Thread.currentThread().getId();
            }
            TypeDescription.Generic generic = TypeDescription.Generic.Builder
                    .parameterizedType(field_def.runtime_class, field_def.parameter_Type_classes).build();
            Class loaded = new ByteBuddy().subclass(Object.class).name(classLoader.getBasePackage()+"."+tmpClassName)
                    .defineField(field.getName(), generic, Visibility.PRIVATE).make()
                    .load(classLoader, ClassLoadingStrategy.Default.INJECTION).getLoaded();
            Field temp_field = null;
            try {
                temp_field = loaded.getDeclaredField(field.getName());
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
            Type fieldType = temp_field.getGenericType();
            loaded=null;
            return fieldType;
        }
        return field_def.raw_type;

    }

    /**
     * 解析字段
     *
     * @param field
     * @return
     */
    public static FieldDefinition parseField(Field field,Map<String,Class> parameter_type_corresponds) throws ClassNotFoundException {
        Type type = field.getType();
        Type generic_type = field.getGenericType();
        FieldDefinition fd = new FieldDefinition();
        if (!field.isAccessible()) field.setAccessible(true);
        fd.field = field;
        fd.raw_type = type;
        fd.genericType = generic_type;
        fd.raw_Type_class = ClassUtils.getClass(type.getTypeName());
        fd.runtime_class=fd.raw_Type_class;
        // 如果是泛型
        if (generic_type instanceof ParameterizedType) {
            fd.isGeneric = true;
            fd.class_type=ClassTypeEnum.ParameterizedType;
            Type[] param_types = ((ParameterizedType) generic_type).getActualTypeArguments();
            Class[] parameter_Type_classes = new Class[param_types.length];
            TypeDefinition[] parameter_type_Defines = new TypeDefinition[param_types.length];

            for (int i = 0; i < param_types.length; i++) {
                ClassTypeEnum classType=ClassTypeEnum.valueOf(param_types[i]);

                if (classType==ClassTypeEnum.TypeVariable) {//如果是List<V>这种
                    parameter_Type_classes[i] = parameter_type_corresponds.get(param_types[i].getTypeName());
                    parameter_type_Defines[i] = parseType(parameter_Type_classes[i]);
                }else{
                    parameter_type_Defines[i] = parseType(param_types[i]);
                }
            }
            fd.parameter_type_Defines=parameter_type_Defines;
            fd.parameter_Type_classes = parameter_Type_classes;
        }else if(fd.raw_type instanceof TypeVariable){//如果是V v这种
            TypeVariable _type= (TypeVariable) fd.raw_type;
            fd.class_type=ClassTypeEnum.TypeVariable;
            fd.runtime_class=parameter_type_corresponds.get(_type.getTypeName());
        }else if(fd.raw_type instanceof WildcardType){//如果是List<?>这种
            fd.isGeneric = true;
            fd.class_type=ClassTypeEnum.WildcardType;
            WildcardType wildcardType = (WildcardType) type;
            Type[] upperBounds = wildcardType.getUpperBounds();
            if (ObjectUtils.isNotEmpty(upperBounds)) {
                fd.upperBounds = parseType((Class) upperBounds[0]);
            }
            Type[] lowerBounds = wildcardType.getLowerBounds();
            if (ObjectUtils.isNotEmpty(lowerBounds)) {
                fd.lowerBounds = parseType((Class) lowerBounds[0]);
            }
        }else if(fd.class_type==ClassTypeEnum.Class){
            fd.class_type=ClassTypeEnum.Class;
            Class clazz = (Class) type;
            //判断是否是数组
            if (clazz.getComponentType() != null) {
                fd.isArray = true;
                TypeDefinition componetClassDefine = parseType(clazz.getComponentType());
                fd.componetClassDefine = componetClassDefine;
            }
        }
        fd.setData_type(DataTypeEnum.valueOf(fd.runtime_class));
        // 判断是否是基本类型
        fd.isPrimitive = fd.raw_Type_class.isPrimitive();
        // 判断是否是基本封装类型
        fd.isPrimitiveWrapper = ClassUtils.isPrimitiveWrapper(fd.raw_Type_class);
        if(fd.isPrimitive){
            fd.runtime_class=ClassUtils.primitiveToWrapper(fd.runtime_class);
        }
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
            if (timeFormat != null) {
                fd.timeFormat = timeFormat.value();
            }
            Timemode timeMode = field.getAnnotation(Timemode.class);
            if (timeMode != null) {
                fd.timeMode = timeMode.value();
            }else{
                fd.timeMode=TimeMode.CURRENT;
            }
        }
        // 判断是否是可序列化字段
        if (!isSerializableField(field)) {
            fd.isSerializable = false;
        }
        // 解析枚举
        EnumFormat[] enumFormats=field.getAnnotationsByType(EnumFormat.class);
        Map<Class,EnumInfo> enumInfos=new HashMap();
        if(ObjectUtils.isNotEmpty(enumFormats)){
            EnumInfo info;
            for (int i = 0; i < enumFormats.length; i++) {
                EnumFormat enumFormat=enumFormats[i];
                info=new EnumInfo();
                info.id=enumFormat.id()==Object.class?fd.field.getDeclaringClass():enumFormat.id();
                if(Enum.class.isAssignableFrom(fd.runtime_class)){
                    info.enum_clazz=fd.runtime_class;
                }else {
                    info.enum_clazz=enumFormat.enum_class()==Object.class?null:enumFormat.enum_class();
                }
                info.define_clazz=fd.runtime_class;
                info.from_field= enumFormat.from_field();
                info.self_field= enumFormat.self_field();
                info.to_field= enumFormat.to_field();
                enumInfos.put(info.id,info);
            }
        }else if(Enum.class.isAssignableFrom(fd.runtime_class)){
            EnumInfo info=new EnumInfo();
            info.id=null;
            info.define_clazz=fd.runtime_class;
            info.enum_clazz=fd.runtime_class;
            enumInfos.put(fd.field.getDeclaringClass(),info);
        }
        fd.enumInfos=enumInfos;
        return fd;
    }

    /**
     * 根据Class生成对象实例
     * <br>
     * 注意:不支持静态类，生成对象实例成功的前提是该Class有默认的构造方法
     * @param clazz
     * @return
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     */
    public static Object newInstance(Class clazz) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        boolean isInnerClass=clazz.isMemberClass();
        if(isInnerClass){//如果是内部类
            Class outerClass=clazz.getEnclosingClass();
            Object outObj=newInstance(outerClass);
            Constructor<?> constructor=clazz.getDeclaredConstructor(outerClass);
            constructor.setAccessible(true);
            return constructor.newInstance(outObj);
        }else if(isCollection(clazz)){
            if(List.class.isAssignableFrom(clazz)){
                return Lists.newArrayList();
            }
            if(Set.class.isAssignableFrom(clazz)){
                return Sets.newHashSet();
            }
            if(Queue.class.isAssignableFrom(clazz)){
                return Queues.newArrayDeque();
            }
        }else{
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            // 创建对象实例
            return constructor.newInstance();
        }
        throw new RuntimeException("该类型"+clazz.getName()+"不支持生成实例！");
    }
}