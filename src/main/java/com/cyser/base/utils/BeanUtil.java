package com.cyser.base.utils;

import com.cyser.base.bean.CopyDefinition;
import com.cyser.base.bean.FieldDefinition;
import com.cyser.base.bean.TypeDefinition;
import com.cyser.base.cache.BeanConvertCache;
import com.cyser.base.cache.TimeConvertCache;
import com.cyser.base.enums.ClassTypeEnum;
import com.cyser.base.enums.CopyFeature;
import com.cyser.base.function.PentaFunction;
import com.cyser.base.function.TernaryFunction;
import com.cyser.base.param.CopyParam;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;

@Slf4j
public class BeanUtil {

    private BeanUtil() {
    }

/*
    public static void copy(Object target, Object[] source, Collection<String> exclude_fields, CopyFeature.CopyFeatureHolder... cfh) {
        if (target == null)
            throw new IllegalArgumentException("对象为null,停止复制!");

        try {
            CopyFeature.CopyFeatureHolder copyFeatureHolder;
            if (ObjectUtils.isEmpty(cfh))
                copyFeatureHolder = new CopyFeature.CopyFeatureHolder();
            else
                copyFeatureHolder = cfh[0];
            Class clazz = target.getClass(); // 目标对象类
            if (ObjectUtils.isNotEmpty(source)) {
                // 获取目标类字段
                Map<String, FieldDefinition> serial_dest_fd_map =
                        CopyableFieldsCache.getSerialFieldDefinitions(clazz);
                if (serial_dest_fd_map.size() == 0) {
                    throw new IllegalArgumentException("目标类[" + clazz.getName() + "]未包含任何可序列化字段.");
                }

                Class src_clazz;
                loop_source_obj:
                for (Object src : source) {
                    if (src != null) {
                        src_clazz = src.getClass();
                        // 校验源对象类型
                        if (ClassUtils.isPrimitiveOrWrapper(src_clazz)
                                || src_clazz == Object.class
                                || src_clazz == Enum.class
                                || src_clazz == String.class
                                || src_clazz == Serializable.class)
                            throw new IllegalArgumentException("对象类型[" + src_clazz.getName() + "]不支持被复制.");

                        // 获取源对象字段
                        Map<String, FieldDefinition> serial_src_fd_map =
                                CopyableFieldsCache.getSerialFieldDefinitions(src_clazz);
                        Collection<FieldDefinition> serial_src_fd_list; // 源对象所有可序列化字段
                        if (serial_src_fd_map.size() == 0) {
                            continue loop_source_obj;
                        } else serial_src_fd_list = serial_src_fd_map.values();

                        if (ObjectUtils.isNotEmpty(exclude_fields)) {
                            serial_src_fd_list =
                                    serial_src_fd_list.stream()
                                            .filter(o -> exclude_fields.contains(o.field.getName()))
                                            .collect(Collectors.toList());
                        }
                        // 复制源对象字段值到目标对象
                        FieldDefinition tmp_src_fd, tmp_dest_fd;
                        Iterator<FieldDefinition> serial_src_fd_itera = serial_src_fd_list.iterator();
                        loop_copy_fields:
                        while (serial_src_fd_itera.hasNext()) {
                            tmp_src_fd = serial_src_fd_itera.next(); // 源对象字段
                            tmp_dest_fd = serial_dest_fd_map.get(tmp_src_fd.field.getName()); // 目标对象字段
                            if (tmp_dest_fd != null) {
                                Object _f_dest_val = tmp_dest_fd.field.get(target); // 目标对象字段值
                                Object _f_src_val = tmp_src_fd.field.get(src); // 源对象字段值

                                // 如果不拷贝空值
                                if (_f_src_val == null
                                        && !copyFeatureHolder.isEnabled(CopyFeature.COPY_NULL_VALUE)) {
                                    continue loop_copy_fields;
                                }
                                // 如果没有启用强制覆盖
                                if (_f_dest_val != null
                                        && !copyFeatureHolder.isEnabled(CopyFeature.FORCE_OVERWRITE)) {
                                    continue loop_copy_fields;
                                }
                                // 如果两者类型相同，直接赋值
                                if (ClassUtils.isAssignable(
                                        tmp_src_fd.raw_Type_class, tmp_dest_fd.raw_Type_class, true)) {
                                    tmp_dest_fd.field.set(target, _f_src_val);
                                } else {
                                    Class<?> _src_clazz = tmp_src_fd.raw_Type_class;
                                    Class<?> _dest_clazz = tmp_dest_fd.raw_Type_class;
                                    if (tmp_src_fd.isPrimitive) {
                                        _src_clazz = ClassUtils.primitiveToWrapper(_src_clazz);
                                    }
                                    if (tmp_dest_fd.isPrimitive) {
                                        _dest_clazz = ClassUtils.primitiveToWrapper(_dest_clazz);
                                    }
                                    // 两个字段是否有一个是时间类型
                                    boolean t1 = tmp_src_fd.isTime || tmp_dest_fd.isTime;
                                    // 源字段是字符串，并且目标字段是基本或者封装类型，并且目标字段不是空类型或者布尔类型
                                    boolean t2 =
                                            _src_clazz.isAssignableFrom(String.class)
                                                    && ClassUtils.isPrimitiveOrWrapper(_dest_clazz)
                                                    && (!(Void.TYPE.equals(_dest_clazz) || Boolean.TYPE.equals(_dest_clazz)));
                                    // 目标字段是字符串，并且源字段是基本或者封装类型，并且源字段不是空类型或者布尔类型
                                    boolean t3 =
                                            _dest_clazz.isAssignableFrom(String.class)
                                                    && ClassUtils.isPrimitiveOrWrapper(_src_clazz)
                                                    && (!(Void.TYPE.equals(_src_clazz) || Boolean.TYPE.equals(_src_clazz)));
                                    if (!(t1 || t2 || t3))
                                        throw new RuntimeException("字段" + tmp_src_fd.field.getName() + "类型不匹配，无法赋值!");
                                    if (t1 && _f_src_val != null) { // 如果有日期类型
                                        assginValue_DateTime(tmp_src_fd, tmp_dest_fd, target, _f_src_val);
                                    } else if (t2 && _f_src_val != null) { // String转基本类型或者封装类型
                                        if (Character.TYPE.isAssignableFrom(_dest_clazz)) {
                                            if (String.valueOf(_f_src_val).length() > 1)
                                                throw new RuntimeException(
                                                        "字段" + tmp_src_fd.field.getName() + "数据长度大于一，无法给char或者Character类型赋值!");
                                            tmp_dest_fd.field.set(target, _f_src_val);
                                        } else if (Short.TYPE.isAssignableFrom(_dest_clazz)) {
                                            tmp_dest_fd.field.set(target, Short.valueOf(String.valueOf(_f_src_val)));
                                        } else if (Integer.TYPE.isAssignableFrom(_dest_clazz)) {
                                            tmp_dest_fd.field.set(target, Integer.valueOf(String.valueOf(_f_src_val)));
                                        } else if (Float.TYPE.isAssignableFrom(_dest_clazz)) {
                                            tmp_dest_fd.field.set(target, Float.valueOf(String.valueOf(_f_src_val)));
                                        } else if (Double.TYPE.isAssignableFrom(_dest_clazz)) {
                                            tmp_dest_fd.field.set(target, Double.valueOf(String.valueOf(_f_src_val)));
                                        } else if (Long.TYPE.isAssignableFrom(_dest_clazz)) {
                                            tmp_dest_fd.field.set(target, Long.valueOf(String.valueOf(_f_src_val)));
                                        }
                                    } else if (t3 && _f_src_val != null) { // 基本类型或者封装类型转String
                                        tmp_dest_fd.field.set(target, String.valueOf(_f_src_val == null ? "" : _f_src_val));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
*/

    private static void copyArrayOnClass(Object target, Object src, TypeDefinition dest_type_def, TypeDefinition src_type_def, Collection<String> exclude_fields, CopyFeature.CopyFeatureHolder... cfh) {
        if (ObjectUtils.isEmpty(src)) {
            return;
        }
        Object[] src_arr = (Object[]) src;
        Object[] target_arr = new Object[src_arr.length];

        Class _dest_clazz = dest_type_def.componetClassDefine.runtime_class;
        Class _src_clazz = src_type_def.componetClassDefine.runtime_class;
        if (dest_type_def.componetClassDefine.isPrimitive) {
            _dest_clazz = ClassUtils.primitiveToWrapper(_dest_clazz);
        }
        if (src_type_def.componetClassDefine.isPrimitive) {
            _src_clazz = ClassUtils.primitiveToWrapper(_src_clazz);
        }
        // 源字段是字符串，并且目标字段是基本或者封装类型，并且目标字段不是空类型或者布尔类型
        boolean t2 =
                _src_clazz.isAssignableFrom(String.class)
                        && ClassUtils.isPrimitiveOrWrapper(_dest_clazz)
                        && (!(Void.TYPE.equals(_dest_clazz) || Boolean.TYPE.equals(_dest_clazz)));
        // 目标字段是字符串，并且源字段是基本或者封装类型，并且源字段不是空类型或者布尔类型
        boolean t3 =
                _dest_clazz.isAssignableFrom(String.class)
                        && ClassUtils.isPrimitiveOrWrapper(_src_clazz)
                        && (!(Void.TYPE.equals(_src_clazz) || Boolean.TYPE.equals(_src_clazz)));

        for (int i = 0; i < src_arr.length; i++) {
            Object _f_src_val = src_arr[i];
            Object _f_dest_val = target_arr[i] != null ? target_arr[i] : new Object();
            if (t2) {
                if (Character.TYPE.isAssignableFrom(_dest_clazz)) {
                    if (String.valueOf(src_arr[i]).length() > 1)
                        throw new RuntimeException(
                                "值" + _f_src_val + "数据长度大于一，无法给char或者Character类型赋值!");
                    _f_dest_val = _f_src_val;
                } else if (Short.TYPE.isAssignableFrom(_dest_clazz)) {
                    _f_dest_val = Short.valueOf(String.valueOf(_f_src_val));
                } else if (Integer.TYPE.isAssignableFrom(_dest_clazz)) {
                    _f_dest_val = Integer.valueOf(String.valueOf(_f_src_val));
                } else if (Float.TYPE.isAssignableFrom(_dest_clazz)) {
                    _f_dest_val = Float.valueOf(String.valueOf(_f_src_val));
                } else if (Double.TYPE.isAssignableFrom(_dest_clazz)) {
                    _f_dest_val = Double.valueOf(String.valueOf(_f_src_val));
                } else if (Long.TYPE.isAssignableFrom(_dest_clazz)) {
                    _f_dest_val = Long.valueOf(String.valueOf(_f_src_val));
                }
                target_arr[i] = _f_dest_val;
            } else if (t3) {
                target_arr[i] = String.valueOf(_f_src_val == null ? "" : _f_src_val);
            } else {
//                copyEntityOnNoParadigm(_f_dest_val, _f_src_val, _dest_clazz, _src_clazz, exclude_fields, cfh);
                target_arr[i] = _f_dest_val;
            }
        }
        target = target_arr;
    }

    private static Object copyCollection(Object target, Object src, TypeDefinition dest_type_def, TypeDefinition src_type_def, Collection<String> exclude_fields, CopyFeature.CopyFeatureHolder... cfh) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (ObjectUtils.isEmpty(src)) {
            return target;
        }
        Collection src_collection = (Collection) src;
        Class _dest_clazz = dest_type_def.runtime_class;
        Class _src_clazz = src_type_def.runtime_class;
        Class _dest_Element_clazz = null,_src_Element_clazz = null;
        if(dest_type_def.isGeneric){
             _dest_Element_clazz=dest_type_def.parameter_type_Defines[0].runtime_class;
            _src_Element_clazz=src_type_def.parameter_type_Defines[0].runtime_class;
            if (dest_type_def.parameter_type_Defines[0].isPrimitive) {
                _dest_Element_clazz = ClassUtils.primitiveToWrapper(_dest_Element_clazz);
            }
            if (src_type_def.parameter_type_Defines[0].isPrimitive) {
                _src_Element_clazz = ClassUtils.primitiveToWrapper(_src_Element_clazz);
            }
        }

        if (ObjectUtils.allNull(target)) {
            Constructor<?> constructor = _dest_clazz.getDeclaredConstructor();
            // 创建对象实例
            target = constructor.newInstance();
        }
        Collection dest_collection = (Collection) target;
        // 源字段是字符串，并且目标字段是基本或者封装类型，并且目标字段不是空类型或者布尔类型
        boolean t2 =
                _src_Element_clazz.isAssignableFrom(String.class)
                        && ClassUtils.isPrimitiveOrWrapper(_dest_Element_clazz)
                        && (!(Void.TYPE.equals(_dest_Element_clazz) || Boolean.TYPE.equals(_dest_Element_clazz)));
        // 目标字段是字符串，并且源字段是基本或者封装类型，并且源字段不是空类型或者布尔类型
        boolean t3 =
                _dest_Element_clazz.isAssignableFrom(String.class)
                        && ClassUtils.isPrimitiveOrWrapper(_src_Element_clazz)
                        && (!(Void.TYPE.equals(_src_Element_clazz) || Boolean.TYPE.equals(_src_Element_clazz)));
        Iterator src_itera = src_collection.iterator();
        while (src_itera.hasNext()) {
            Object _f_src_val = src_itera.next();
            if (t2) {
                if (Character.class.isAssignableFrom(_dest_Element_clazz)) {
                    if (String.valueOf(_f_src_val).length() > 1)
                        throw new RuntimeException(
                                "值" + _f_src_val + "数据长度大于一，无法给char或者Character类型赋值!");
                    dest_collection.add(_f_src_val);
                } else if (Short.class.isAssignableFrom(_dest_Element_clazz)) {
                    dest_collection.add( Short.valueOf(String.valueOf(_f_src_val)));
                } else if (Integer.class.isAssignableFrom(_dest_Element_clazz)) {
                    dest_collection.add(Integer.valueOf(String.valueOf(_f_src_val)));
                } else if (Float.class.isAssignableFrom(_dest_Element_clazz)) {
                    dest_collection.add(Float.valueOf(String.valueOf(_f_src_val)));
                } else if (Double.class.isAssignableFrom(_dest_Element_clazz)) {
                    dest_collection.add( Double.valueOf(String.valueOf(_f_src_val)));
                } else if (Long.class.isAssignableFrom(_dest_Element_clazz)) {
                    dest_collection.add( Long.valueOf(String.valueOf(_f_src_val)));
                }
            } else if (t3) {
                dest_collection.add( String.valueOf(_f_src_val == null ? "" : _f_src_val));
            } else {
                Constructor<?> constructor = _dest_Element_clazz.getDeclaredConstructor();
                // 创建对象实例
                Object target_obj= constructor.newInstance();
//                copyEntityOnNoParadigm(target_obj, _f_src_val, _dest_Element_clazz, _src_Element_clazz, exclude_fields, cfh);
                dest_collection.add(target_obj);
            }
        }
        return target;
    }

    /**
     * 复制没有范型的实体类
     *
     * @param target
     */
/*
    public static void copyEntityOnNoParadigm(Object target, Object src, Class clazz, Class src_clazz, Collection<String> exclude_fields, CopyFeature.CopyFeatureHolder... cfh) {
        if (target == null)
            throw new IllegalArgumentException("对象为null,停止复制!");

        try {
            CopyFeature.CopyFeatureHolder copyFeatureHolder;
            if (ObjectUtils.isEmpty(cfh))
                copyFeatureHolder = new CopyFeature.CopyFeatureHolder();
            else
                copyFeatureHolder = cfh[0];
            if (clazz == null) {
                clazz = target.getClass(); // 目标对象类
            }
            if (ObjectUtils.isNotEmpty(src)) {
                // 获取目标类字段
                Map<String, FieldDefinition> serial_dest_fd_map =
                        CopyableFieldsCache.getSerialFieldDefinitions(clazz);
                if (serial_dest_fd_map.size() == 0) {
                    throw new IllegalArgumentException("目标类[" + clazz.getName() + "]未包含任何可序列化字段.");
                }

                if (src != null) {
                    if (src_clazz == null) {
                        src_clazz = src.getClass();//源对象类
                    }
                    // 校验源对象类型
                    if (ClassUtils.isPrimitiveOrWrapper(src_clazz)
                            || src_clazz == Object.class
                            || src_clazz == Enum.class
                            || src_clazz == String.class
                            || src_clazz == Serializable.class)
                        throw new IllegalArgumentException("对象类型[" + src_clazz.getName() + "]不支持被复制.");

                    // 获取源对象字段
                    Map<String, FieldDefinition> serial_src_fd_map =
                            CopyableFieldsCache.getSerialFieldDefinitions(src_clazz);
                    Collection<FieldDefinition> serial_src_fd_list; // 源对象所有可序列化字段
                    if (serial_src_fd_map.size() == 0) {
                        return;
                    } else serial_src_fd_list = serial_src_fd_map.values();

                    if (ObjectUtils.isNotEmpty(exclude_fields)) {
                        serial_src_fd_list =
                                serial_src_fd_list.stream()
                                        .filter(o -> exclude_fields.contains(o.field.getName()))
                                        .collect(Collectors.toList());
                    }
                    // 复制源对象字段值到目标对象
                    FieldDefinition tmp_src_fd, tmp_dest_fd;
                    Iterator<FieldDefinition> serial_src_fd_itera = serial_src_fd_list.iterator();
                    loop_copy_fields:
                    while (serial_src_fd_itera.hasNext()) {
                        tmp_src_fd = serial_src_fd_itera.next(); // 源对象字段
                        tmp_dest_fd = serial_dest_fd_map.get(tmp_src_fd.field.getName()); // 目标对象字段
                        if (tmp_dest_fd != null) {
                            Object _f_dest_val = tmp_dest_fd.field.get(target); // 目标对象字段值
                            Object _f_src_val = tmp_src_fd.field.get(src); // 源对象字段值

                            // 如果不拷贝空值
                            if (_f_src_val == null
                                    && !copyFeatureHolder.isEnabled(CopyFeature.COPY_NULL_VALUE)) {
                                continue loop_copy_fields;
                            }
                            // 如果没有启用强制覆盖
                            if (_f_dest_val != null
                                    && !copyFeatureHolder.isEnabled(CopyFeature.FORCE_OVERWRITE)) {
                                continue loop_copy_fields;
                            }
                            // 如果两者类型相同，直接赋值
                            if (ClassUtils.isAssignable(
                                    tmp_src_fd.raw_Type_class, tmp_dest_fd.raw_Type_class, true)) {
                                tmp_dest_fd.field.set(target, _f_src_val);
                            } else {
                                Class<?> _src_clazz = tmp_src_fd.raw_Type_class;
                                Class<?> _dest_clazz = tmp_dest_fd.raw_Type_class;
                                if (tmp_src_fd.isPrimitive) {
                                    _src_clazz = ClassUtils.primitiveToWrapper(_src_clazz);
                                }
                                if (tmp_dest_fd.isPrimitive) {
                                    _dest_clazz = ClassUtils.primitiveToWrapper(_dest_clazz);
                                }
                                // 两个字段是否有一个是时间类型
                                boolean t1 = tmp_src_fd.isTime || tmp_dest_fd.isTime;
                                // 源字段是字符串，并且目标字段是基本或者封装类型，并且目标字段不是空类型或者布尔类型
                                boolean t2 =
                                        _src_clazz.isAssignableFrom(String.class)
                                                && ClassUtils.isPrimitiveOrWrapper(_dest_clazz)
                                                && (!(Void.TYPE.equals(_dest_clazz) || Boolean.TYPE.equals(_dest_clazz)));
                                // 目标字段是字符串，并且源字段是基本或者封装类型，并且源字段不是空类型或者布尔类型
                                boolean t3 =
                                        _dest_clazz.isAssignableFrom(String.class)
                                                && ClassUtils.isPrimitiveOrWrapper(_src_clazz)
                                                && (!(Void.TYPE.equals(_src_clazz) || Boolean.TYPE.equals(_src_clazz)));
                                if (!(t1 || t2 || t3))
                                    throw new RuntimeException("字段" + tmp_src_fd.field.getName() + "类型不匹配，无法赋值!");
                                if (t1 && _f_src_val != null) { // 如果有日期类型
                                    assginValue_DateTime(tmp_src_fd, tmp_dest_fd, target, _f_src_val);
                                } else if (t2 && _f_src_val != null) { // String转基本类型或者封装类型
                                    if (Character.class.isAssignableFrom(_dest_clazz)) {
                                        if (String.valueOf(_f_src_val).length() > 1)
                                            throw new RuntimeException(
                                                    "字段" + tmp_src_fd.field.getName() + "数据长度大于一，无法给char或者Character类型赋值!");
                                        tmp_dest_fd.field.set(target, _f_src_val);
                                    } else if (Short.class.isAssignableFrom(_dest_clazz)) {
                                        tmp_dest_fd.field.set(target, Short.valueOf(String.valueOf(_f_src_val)));
                                    } else if (Integer.class.isAssignableFrom(_dest_clazz)) {
                                        tmp_dest_fd.field.set(target, Integer.valueOf(String.valueOf(_f_src_val)));
                                    } else if (Float.class.isAssignableFrom(_dest_clazz)) {
                                        tmp_dest_fd.field.set(target, Float.valueOf(String.valueOf(_f_src_val)));
                                    } else if (Double.class.isAssignableFrom(_dest_clazz)) {
                                        tmp_dest_fd.field.set(target, Double.valueOf(String.valueOf(_f_src_val)));
                                    } else if (Long.class.isAssignableFrom(_dest_clazz)) {
                                        tmp_dest_fd.field.set(target, Long.valueOf(String.valueOf(_f_src_val)));
                                    }
                                } else if (t3 && _f_src_val != null) { // 基本类型或者封装类型转String
                                    tmp_dest_fd.field.set(target, String.valueOf(_f_src_val == null ? "" : _f_src_val));
                                }
                            }
                        }
                    }
                }

            }
        } catch (IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
*/

    public static void copy(Object target, Object[] source, CopyParam cp) {
    }

    public static void copy(Object target, Object source, CopyParam... cp) {
        copy(target, source, target.getClass(), source.getClass(), cp);
    }

    private static void copy(Object target, Object source, Class dest_clazz, Class src_clazz, CopyParam... cp) {
        if (ObjectUtils.isEmpty(source)) {
            log.warn("被复制对象为空，停止复制。");
            return;
        }
        if (!ClassUtil.isSerializableClass(dest_clazz)) {
            throw new RuntimeException("检查类[" + dest_clazz.getName() + "]是否是final、static、abstract,停止复制值！");
        }
        if (!ClassUtil.isSerializableClass(src_clazz)) {
            throw new RuntimeException("检查类[" + src_clazz.getName() + "]是否是final、static、abstract,停止复制值！");
        }
        if (ClassUtil.isGenericClass(dest_clazz) || ClassUtil.isGenericClass(src_clazz)) {
            throw new RuntimeException("检查类[" + src_clazz.getName() + "," + dest_clazz.getName()
                    + "]是否是范型类,停止复制值！请尝试调用[void copy(Object target, Object source, TypeReference dest_tr, TypeReference src_tr, CopyParam ... cp)]方法。");
        }
        //todo

    }

    public static Object copy(Object target, Object source, TypeReference dest_tr, TypeReference src_tr, CopyParam... cps) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (!ObjectUtils.allNotNull(dest_tr, src_tr)) {
            throw new RuntimeException("参数为空,停止复制值！");
        }
        CopyParam cp;
        if (ObjectUtils.isNotEmpty(cps)) {
            cp = cps[0];
        } else {
            cp = new CopyParam();
        }

        Type dest_type = dest_tr.getType();
        Type src_type = src_tr.getType();
        TypeDefinition dest_type_def = ClassUtil.parseType(dest_type);
        TypeDefinition src_type_def = ClassUtil.parseType(src_type);
        if (dest_type_def.class_type == ClassTypeEnum.Class && dest_type_def.isGeneric) {
            throw new RuntimeException("因为类[" + dest_type_def.runtime_class.getName() + "]带有范型，请正确传参数dest_tr.");
        }
        if (src_type_def.class_type == ClassTypeEnum.Class && src_type_def.isGeneric) {
            throw new RuntimeException("因为类[" + src_type_def.runtime_class.getName() + "]带有范型，请正确传参数src_tr.");
        }

        PentaFunction<Object, Object, TypeDefinition, TypeDefinition, CopyParam, Object> copy_opera= BeanConvertCache.bean_method_table.get(dest_type_def.data_type,src_type_def.data_type);
        target=copy_opera.apply(target,source,dest_type_def,src_type_def,cp);

        return target;
    }

    private static void assginValue_DateTime(
            FieldDefinition src_fd, FieldDefinition dest_fd, Object target, Object src_val)
            throws IllegalAccessException {
        Class<?> _src_clazz = src_fd.raw_Type_class;
        Class<?> _dest_clazz = dest_fd.raw_Type_class;
        if (src_fd.isPrimitive) {
            _src_clazz = ClassUtils.primitiveToWrapper(_src_clazz);
        }
        if (dest_fd.isPrimitive) {
            _dest_clazz = ClassUtils.primitiveToWrapper(_dest_clazz);
        }
        TernaryFunction<FieldDefinition, FieldDefinition, Object, Object> method =
                TimeConvertCache.time_method_table.get(_dest_clazz, _src_clazz);
        dest_fd.field.set(target, method.apply(src_fd, dest_fd, src_val));
    }
}
