package com.cyser.base;

import com.cyser.base.enums.CopyFeature;
import com.cyser.base.function.TernaryFunction;
import com.cyser.base.utils.ClassUtil;
import com.cyser.base.utils.TimestampUtil;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public interface Copyable<T> {

    /**
     * 字段缓存
     */
    Map<String, Map<String, FieldDefinition>> FIELDS_CACHE = Maps.newHashMap();

    default  T copy(Collection<String> exclude_fields, Object... source) {
        return copy(null, exclude_fields, source);
    }


    default <T extends Copyable, E> T copy(CopyFeature[] features, Collection<String> exclude_fields, Object... source) {
        try {

            int features_value = CopyFeature.CopyFeatureHolder.calculateFeatures(features);

            Class clazz = this.getClass();//当前类
            if (ObjectUtils.isNotEmpty(source)) {
                //获取当前类字段
                Map<String, FieldDefinition> serial_dest_fd_map = FIELDS_CACHE.get(clazz.getName());
                if (serial_dest_fd_map == null) {
                    Collection<Field> all_dest_fields_list = ClassUtil.getAllFieldsCollection(clazz);//目标类所有字段
                    if (ObjectUtils.isEmpty(all_dest_fields_list))
                        throw new IllegalArgumentException("当前类[" + clazz.getName() + "]未包含任何字段");
                    Collection<FieldDefinition> serial_dest_fd_list;//目标类可序列化字段
                    List<FieldDefinition> all_dest_fd_list = new ArrayList<>();
                    for (Field field : all_dest_fields_list) {
                        all_dest_fd_list.add(ClassUtil.parseField(field));
                    }
                    serial_dest_fd_list = all_dest_fd_list.stream().filter(o -> o.isSerializable).collect(Collectors.toList());
                    if (ObjectUtils.isEmpty(serial_dest_fd_list)) {
                        FIELDS_CACHE.put(clazz.getName(), new HashMap<>());
                        throw new IllegalArgumentException("当前类[" + clazz.getName() + "]未包含任何可序列化字段");
                    }
                    serial_dest_fd_map = Maps.uniqueIndex(serial_dest_fd_list, o -> o.field.getName());
                    FIELDS_CACHE.put(clazz.getName(), serial_dest_fd_map);
                }

                Class src_clazz;
                loop_source_obj:
                for (Object src : source) {
                    if (src != null) {
                        src_clazz = src.getClass();
                        //校验源对象类型
                        if (ClassUtils.isPrimitiveOrWrapper(src_clazz)
                                || src_clazz == Object.class
                                || src_clazz == Enum.class
                                || src_clazz == String.class
                                || src_clazz == Serializable.class)
                            throw new IllegalArgumentException("对象类型[" + src_clazz.getName() + "]不支持被复制");

                        //获取源对象字段
                        Map<String, FieldDefinition> serial_src_fd_map = FIELDS_CACHE.get(src_clazz.getName());
                        Collection<FieldDefinition> serial_src_fd_list;//源对象所有可序列化字段
                        if (serial_src_fd_map == null) {
                            List<Field> all_src_fields_list = FieldUtils.getAllFieldsList(src_clazz);//源对象所有字段
                            if (ObjectUtils.isEmpty(all_src_fields_list))
                                continue loop_source_obj;
                            List<FieldDefinition> all_src_fd_list = new ArrayList<>();//源对象所有字段定义
                            for (Field field : all_src_fields_list) {
                                all_src_fd_list.add(ClassUtil.parseField(field));
                            }
                            serial_src_fd_list = all_src_fd_list.stream().filter(o -> o.isSerializable).collect(Collectors.toList());
                            if (ObjectUtils.isEmpty(serial_src_fd_list)) {
                                FIELDS_CACHE.put(src_clazz.getName(), new HashMap<>());
                                continue loop_source_obj;
                            }
                            serial_src_fd_map = Maps.uniqueIndex(Sets.newHashSet(serial_src_fd_list), o -> o.field.getName());
                            FIELDS_CACHE.put(src_clazz.getName(), serial_src_fd_map);
                        } else
                            serial_src_fd_list = serial_src_fd_map.values();

                        if (ObjectUtils.isNotEmpty(exclude_fields)) {
                            serial_src_fd_list = serial_src_fd_list.stream().filter(o -> exclude_fields.contains(o.field.getName())).collect(Collectors.toList());
                        }
                        //复制源对象字段值到目标对象
                        FieldDefinition tmp_src_fd, tmp_dest_fd;
                        Iterator<FieldDefinition> serial_src_fd_itera = serial_src_fd_list.iterator();
                        loop_copy_fields:
                        while (serial_src_fd_itera.hasNext()) {
                            tmp_src_fd = serial_src_fd_itera.next();
                            tmp_dest_fd = serial_dest_fd_map.get(tmp_src_fd.field.getName());
                            if (tmp_dest_fd != null) {
                                Object dest_val = tmp_dest_fd.field.get(this);//目标对象字段值
                                Object src_val = tmp_src_fd.field.get(src);//源对象字段值

                                //如果不拷贝空值
                                if (src_val == null &&
                                        !CopyFeature.CopyFeatureHolder.isEnabled(features_value, CopyFeature.COPY_NULL_VALUE)) {
                                    continue loop_copy_fields;
                                }
                                //如果没有启用强制覆盖
                                if (dest_val != null &&
                                        !CopyFeature.CopyFeatureHolder.isEnabled(features_value, CopyFeature.FORCE_OVERWRITE)) {
                                    continue loop_copy_fields;
                                }
                                //如果两者类型相同，直接赋值
                                if (ClassUtils.isAssignable(tmp_src_fd.raw_Type_class, tmp_dest_fd.raw_Type_class, true)) {
                                    tmp_dest_fd.field.set(this, src_val);
                                } else {
                                    Class<?> _src_clazz = tmp_src_fd.raw_Type_class;
                                    Class<?> _dest_clazz = tmp_dest_fd.raw_Type_class;
                                    if (tmp_src_fd.isPrimitive) {
                                        _src_clazz = ClassUtils.primitiveToWrapper(_src_clazz);
                                    }
                                    if (tmp_dest_fd.isPrimitive) {
                                        _dest_clazz = ClassUtils.primitiveToWrapper(_dest_clazz);
                                    }
                                    //两个字段是否有一个是时间类型
                                    boolean t1 = tmp_src_fd.isTime || tmp_dest_fd.isTime;
                                    //源字段是字符串，并且目标字段是基本或者封装类型，并且目标字段不是空类型或者布尔类型
                                    boolean t2 = _src_clazz.isAssignableFrom(String.class)
                                            && ClassUtils.isPrimitiveOrWrapper(_dest_clazz)
                                            && (!(Void.TYPE.equals(_dest_clazz) || Boolean.TYPE.equals(_dest_clazz)));
                                    //目标字段是字符串，并且源字段是基本或者封装类型，并且源字段不是空类型或者布尔类型
                                    boolean t3 = _dest_clazz.isAssignableFrom(String.class)
                                            && ClassUtils.isPrimitiveOrWrapper(_src_clazz)
                                            && (!(Void.TYPE.equals(_src_clazz) || Boolean.TYPE.equals(_src_clazz)));
                                    if (!(t1 || t2 || t3))
                                        throw new RuntimeException("字段" + tmp_src_fd.field.getName() + "类型不匹配，无法赋值!");
                                    if (t1 && src_val != null) {//如果有日期类型
                                        assginValue_DateTime(tmp_src_fd, tmp_dest_fd, src_val, dest_val);
                                    } else if (t2 && src_val != null) {//String转基本类型或者封装类型
                                        if (Character.TYPE.equals(_dest_clazz)) {
                                            if (String.valueOf(src_val).length() > 1)
                                                throw new RuntimeException("字段" + tmp_src_fd.field.getName() + "数据长度大于一，无法给char或者Character类型赋值!");
                                            tmp_dest_fd.field.set(this, Character.valueOf((Character) src_val));
                                        } else if (Short.TYPE.equals(_dest_clazz)) {
                                            tmp_dest_fd.field.set(this, Short.valueOf(String.valueOf(src_val)));
                                        } else if (Integer.TYPE.equals(_dest_clazz)) {
                                            tmp_dest_fd.field.set(this, Integer.valueOf(String.valueOf(src_val)));
                                        } else if (Float.TYPE.equals(_dest_clazz)) {
                                            tmp_dest_fd.field.set(this, Float.valueOf(String.valueOf(src_val)));
                                        } else if (Double.TYPE.equals(_dest_clazz)) {
                                            tmp_dest_fd.field.set(this, Double.valueOf(String.valueOf(src_val)));
                                        } else if (Long.TYPE.equals(_dest_clazz)) {
                                            tmp_dest_fd.field.set(this, Long.valueOf(String.valueOf(src_val)));
                                        }
                                    } else if (t3 && src_val != null) {//基本类型或者封装类型转String
                                        tmp_dest_fd.field.set(this, String.valueOf(src_val == null ? "" : src_val));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return (T) this;
    }

    private void assginValue_DateTime(FieldDefinition src_fd, FieldDefinition dest_fd, Object src_val, Object dest_val) throws IllegalAccessException {
        Class<?> _src_clazz = src_fd.raw_Type_class;
        Class<?> _dest_clazz = dest_fd.raw_Type_class;
        if (src_fd.isPrimitive) {
            _src_clazz = ClassUtils.primitiveToWrapper(_src_clazz);
        }
        if (dest_fd.isPrimitive) {
            _dest_clazz = ClassUtils.primitiveToWrapper(_dest_clazz);
        }
        TernaryFunction<FieldDefinition, FieldDefinition, Object, Object> method = TimeConvertCache.time_method_table.get(_dest_clazz, _src_clazz);
        dest_fd.field.set(this, method.apply(src_fd, dest_fd, src_val));
    }

    class TimeConvertCache {
        /**
         * 时间转换方法缓存
         */
        public static final Table<Class, Class, TernaryFunction<FieldDefinition, FieldDefinition, Object, Object>> time_method_table = HashBasedTable.create();

        static {
            //LocalDate.class
            time_method_table.put(LocalDate.class, LocalDateTime.class, (src_fd, dest_fd, src_val) -> TimestampUtil.toLocalDate((LocalDateTime) src_val));
            time_method_table.put(LocalDate.class, Date.class, (src_fd, dest_fd, src_val) -> TimestampUtil.toLocalDate((Date) src_val));
            time_method_table.put(LocalDate.class, Long.class, (src_fd, dest_fd, src_val) -> TimestampUtil.toLocalDate((Long) src_val));
            time_method_table.put(LocalDate.class, String.class, (src_fd, dest_fd, src_val) -> {
                try {
                    if (!src_fd.isTime)
                        throw new RuntimeException("目标对象字段[" + src_fd.field.getName() + "]不是日期字符串,解决方案:在该字段上加上@TimeFormat");
                    return TimestampUtil.toLocalDate((String) src_val, src_fd.timeFormat);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            });
            //LocalDateTime.class
            time_method_table.put(LocalDateTime.class, LocalDate.class, (src_fd, dest_fd, src_val) -> TimestampUtil.toLocalDateTime((LocalDate) src_val));
            time_method_table.put(LocalDateTime.class, Date.class, (src_fd, dest_fd, src_val) -> TimestampUtil.toLocalDateTime((Date) src_val));
            time_method_table.put(LocalDateTime.class, Long.class, (src_fd, dest_fd, src_val) -> TimestampUtil.toLocalDateTime((Long) src_val));
            time_method_table.put(LocalDateTime.class, String.class, (src_fd, dest_fd, src_val) -> {
                try {
                    if (!src_fd.isTime)
                        throw new RuntimeException("目标对象字段[" + src_fd.field.getName() + "]不是日期字符串,解决方案:在该字段上加上@TimeFormat");
                    return TimestampUtil.toLocalDateTime((String) src_val, src_fd.timeFormat);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            });
            //Date.class
            time_method_table.put(Date.class, LocalDate.class, (src_fd, dest_fd, src_val) -> TimestampUtil.toDate((LocalDate) src_val));
            time_method_table.put(Date.class, LocalDateTime.class, (src_fd, dest_fd, src_val) -> TimestampUtil.toDate((LocalDateTime) src_val));
            time_method_table.put(Date.class, Long.class, (src_fd, dest_fd, src_val) -> TimestampUtil.toDate((Long) src_val));
            time_method_table.put(Date.class, String.class, (src_fd, dest_fd, src_val) -> {
                try {
                    if (!src_fd.isTime)
                        throw new RuntimeException("目标对象字段[" + src_fd.field.getName() + "]不是日期字符串,解决方案:在该字段上加上@TimeFormat");
                    return TimestampUtil.toDate((String) src_val, src_fd.timeFormat);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            });

            //Long.class
            time_method_table.put(Long.class, LocalDate.class, (src_fd, dest_fd, src_val) -> TimestampUtil.getUnixTimeStamp((LocalDate) src_val));
            time_method_table.put(Long.class, LocalDateTime.class, (src_fd, dest_fd, src_val) -> TimestampUtil.getUnixTimeStamp((LocalDateTime) src_val));
            time_method_table.put(Long.class, Date.class, (src_fd, dest_fd, src_val) -> TimestampUtil.getUnixTimeStamp((Date) src_val));
            time_method_table.put(Long.class, String.class, (src_fd, dest_fd, src_val) -> {
                try {
                    if (!src_fd.isTime)
                        throw new RuntimeException("目标对象字段[" + src_fd.field.getName() + "]不是日期字符串,解决方案:在该字段上加上@TimeFormat");
                    return TimestampUtil.getUnixTimeStamp((String) src_val, TimestampUtil.TimeMode.CURRENT, src_fd.timeFormat);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            });

            //String.class
            time_method_table.put(String.class, LocalDate.class, (src_fd, dest_fd, src_val) -> {
                if (!dest_fd.isTime)
                    throw new RuntimeException("目标对象字段[" + dest_fd.field.getName() + "]不是日期字符串,解决方案:在该字段上加上@TimeFormat");
                return TimestampUtil.format((LocalDate) src_val, dest_fd.timeFormat);
            });
            time_method_table.put(String.class, LocalDateTime.class, (src_fd, dest_fd, src_val) -> {
                if (!dest_fd.isTime)
                    throw new RuntimeException("目标对象字段[" + dest_fd.field.getName() + "]不是日期字符串,解决方案:在该字段上加上@TimeFormat");
                return TimestampUtil.format((LocalDateTime) src_val, dest_fd.timeFormat);
            });
            time_method_table.put(String.class, Date.class, (src_fd, dest_fd, src_val) -> {
                if (!dest_fd.isTime)
                    throw new RuntimeException("目标对象字段[" + dest_fd.field.getName() + "]不是日期字符串,解决方案:在该字段上加上@TimeFormat");
                return TimestampUtil.format((Date) src_val, dest_fd.timeFormat);
            });
            time_method_table.put(String.class, Long.class, (src_fd, dest_fd, src_val) -> {
                if (!dest_fd.isTime)
                    throw new RuntimeException("目标对象字段[" + dest_fd.field.getName() + "]不是日期字符串,解决方案:在该字段上加上@TimeFormat");
                return TimestampUtil.format((Long) src_val, dest_fd.timeFormat);
            });
        }
    }
}
