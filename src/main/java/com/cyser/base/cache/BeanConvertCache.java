package com.cyser.base.cache;

import com.cyser.base.bean.ClassDefinition;
import com.cyser.base.bean.CopyDefinition;
import com.cyser.base.bean.FieldDefinition;
import com.cyser.base.enums.CopyFeature;
import com.cyser.base.enums.DataTypeEnum;
import com.cyser.base.function.PentaFunction;
import com.cyser.base.function.TernaryFunction;
import com.cyser.base.param.CopyParam;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class BeanConvertCache {
    /**
     * Bean转换方法缓存
     *
     * @param Object              目标对象值
     * @param Object              被拷贝对象值
     * @param CopyDefinition      返回对象拷贝定义
     * @param CopyDefinition      被拷贝对象拷贝定义
     * @param CopyParam           拷贝参数
     * @param Object              返回对象值
     */
    public static final Table<
            DataTypeEnum, DataTypeEnum, PentaFunction<Object, Object, CopyDefinition, CopyDefinition, CopyParam, Object>>
            bean_method_table = HashBasedTable.create();

    static {
        bean_method_table.put(DataTypeEnum.Entity_Class, DataTypeEnum.Entity_Class, (target,src,target_def,src_def,cp) -> copyEntity2Entity(target,src,target_def,src_def,cp));
    }


    public static Object copyEntity2Entity(Object target, Object src, CopyDefinition _target_def, CopyDefinition _src_def, CopyParam cp) {
        if (target == null)
            throw new IllegalArgumentException("对象为null,停止复制!");
        Collection<String> exclude_fields=cp.exclude_fields;
        CopyFeature.CopyFeatureHolder cfh=cp.copyFeature;
        try {
            ClassDefinition target_def= (ClassDefinition) _target_def;
            ClassDefinition src_def= (ClassDefinition) _src_def;
            Class target_clazz=target_def.clazz;
            if (target_clazz == null) {
                target_clazz = target.getClass(); // 目标对象类
            }
            Class src_clazz=src_def.clazz;
            if (ObjectUtils.isNotEmpty(src)) {
                // 获取目标类字段
                Map<String, FieldDefinition> serial_dest_fd_map =
                        CopyableFieldsCache.getSerialFieldDefinitions(target_clazz);
                if (serial_dest_fd_map.size() == 0) {
                    throw new IllegalArgumentException("目标类[" + target_clazz.getName() + "]未包含任何可序列化字段.");
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
                        log.warn("源对象的可序列化字段为空，停止复制！");
                        return target;
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
                                    && !cfh.isEnabled(CopyFeature.COPY_NULL_VALUE)) {
                                continue loop_copy_fields;
                            }
                            // 如果没有启用强制覆盖
                            if (_f_dest_val != null
                                    && !cfh.isEnabled(CopyFeature.FORCE_OVERWRITE)) {
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
