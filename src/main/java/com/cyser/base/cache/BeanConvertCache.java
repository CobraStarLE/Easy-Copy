package com.cyser.base.cache;

import com.cyser.base.bean.CopyDefinition;
import com.cyser.base.bean.EnumInfo;
import com.cyser.base.bean.FieldDefinition;
import com.cyser.base.bean.TypeDefinition;
import com.cyser.base.enums.CopyFeature;
import com.cyser.base.enums.DataTypeEnum;
import com.cyser.base.function.PentaFunction;
import com.cyser.base.function.TernaryFunction;
import com.cyser.base.param.CopyParam;
import com.cyser.base.utils.BeanUtil;
import com.cyser.base.utils.ClassUtil;
import com.cyser.base.utils.EnumUtil;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class BeanConvertCache {
    /**
     * Bean转换方法缓存
     * 第一个DataTypeEnum 为被复制对象的
     * 第二个DataTypeEnum 为复制对象的
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
        bean_method_table.put(DataTypeEnum.Entity_Class, DataTypeEnum.Entity_Class, (target, src, target_def, src_def, cp) -> copyEntity2Entity(target, src, target_def, src_def, cp));
        bean_method_table.put(DataTypeEnum.Collection, DataTypeEnum.Collection, (target, src, target_def, src_def, cp) -> copyCollection2Collection(target, src, target_def, src_def, cp));
        bean_method_table.put(DataTypeEnum.PrimitiveOrWrapperOrString, DataTypeEnum.Enum, (target, src, target_def, src_def, cp) -> copyPrimitiveOrWrapperOrString2Enum(target, src, target_def, src_def, cp));
        bean_method_table.put(DataTypeEnum.Enum, DataTypeEnum.PrimitiveOrWrapperOrString, (target, src, target_def, src_def, cp) -> copyEnum2PrimitiveOrWrapperOrString(target, src, target_def, src_def, cp));
        bean_method_table.put(DataTypeEnum.Enum, DataTypeEnum.Enum, (target, src, target_def, src_def, cp) -> copyEnum2Enum(target, src, target_def, src_def, cp));
    }

    public static Object copyObject2Object(Object target, Object src, CopyDefinition _target_def, CopyDefinition _src_def, CopyParam cp) {
        TypeDefinition target_def = (TypeDefinition) _target_def;
        TypeDefinition src_def = (TypeDefinition) _src_def;

        if (target == null) {
            try {
                target = ClassUtil.newInstance(target_def.runtime_class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        CopyFeature.CopyFeatureHolder cfh = cp.copyFeature;
        if (ObjectUtils.allNotNull(src) || (ObjectUtils.allNull(src) && cfh.isEnabled(CopyFeature.COPY_NULL_VALUE))) {
            target = src;
        }
        return target;
    }


    public static Object copyEntity2Entity(Object target, Object src, CopyDefinition _target_def, CopyDefinition _src_def, CopyParam cp) {
        TypeDefinition target_def = (TypeDefinition) _target_def;
        TypeDefinition src_def = (TypeDefinition) _src_def;
        if (target == null) {
            try {
                target = ClassUtil.newInstance(target_def.runtime_class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        Collection<String> exclude_fields = cp.exclude_fields;
        CopyFeature.CopyFeatureHolder cfh = cp.copyFeature;
        try {
            Class target_clazz = target_def.runtime_class;
            if (target_clazz == null) {
                target_clazz = target.getClass(); // 目标对象类
            }
            Class src_clazz = src_def.runtime_class;
            if (ObjectUtils.isNotEmpty(src)) {
                // 获取目标类字段
                Map<String, FieldDefinition> serial_dest_fd_map =
                        CopyableFieldsCache.getSerialFieldDefinitions(target_def);
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
                            CopyableFieldsCache.getSerialFieldDefinitions(src_def);
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
                                    tmp_src_fd.runtime_class, tmp_dest_fd.runtime_class, true)) {
                                tmp_dest_fd.field.set(target, _f_src_val);
                            } else {
                                Class<?> _src_clazz = tmp_src_fd.runtime_class;
                                Class<?> _dest_clazz = tmp_dest_fd.runtime_class;
                                // 两个字段是否有一个是时间类型
                                boolean t1 = tmp_src_fd.isTime || tmp_dest_fd.isTime;
                                // 源字段是字符串，并且目标字段是基本或者封装类型，并且目标字段不是空类型或者布尔类型
                                boolean t2 =
                                        String.class.isAssignableFrom(_src_clazz)
                                                && ClassUtils.isPrimitiveOrWrapper(_dest_clazz)
                                                && (!(Void.TYPE.equals(_dest_clazz) || Boolean.TYPE.equals(_dest_clazz)));
                                // 目标字段是字符串，并且源字段是基本或者封装类型，并且源字段不是空类型或者布尔类型
                                boolean t3 =
                                        _dest_clazz.isAssignableFrom(String.class)
                                                && ClassUtils.isPrimitiveOrWrapper(_src_clazz)
                                                && (!(Void.TYPE.equals(_src_clazz) || Boolean.TYPE.equals(_src_clazz)));
                                if (!(t1 || t2 || t3)) {
                                    PentaFunction<Object, Object, CopyDefinition, CopyDefinition, CopyParam, Object> method;
                                    // 如果有枚举
                                    if (tmp_src_fd.isEnum(tmp_dest_fd.field.getDeclaringClass()) || tmp_dest_fd.isEnum(tmp_src_fd.field.getDeclaringClass())) {
                                        method = BeanConvertCache.bean_method_table.get(tmp_src_fd.getData_type(tmp_dest_fd.field.getDeclaringClass()), tmp_dest_fd.getData_type(tmp_src_fd.field.getDeclaringClass()));
                                        if (method != null) {
                                            _f_dest_val=method.apply(_f_dest_val, _f_src_val, tmp_dest_fd, tmp_src_fd, cp);
                                            tmp_dest_fd.field.set(target, _f_dest_val);
                                        } else {
                                            throw new RuntimeException("字段" + tmp_src_fd.field.getName() + "类型不匹配，无法赋值!");
                                        }
                                    } else {
                                        Type runtime_dest_type = ClassUtil.generateRuntimeField(tmp_dest_fd);
                                        Type runtime_src_type = ClassUtil.generateRuntimeField(tmp_src_fd);
                                        TypeDefinition runtime_dest_def = ClassUtil.parseType(runtime_dest_type);
                                        TypeDefinition runtime_src_def = ClassUtil.parseType(runtime_src_type);
                                        method = BeanConvertCache.bean_method_table.get(runtime_src_def.getData_type(), runtime_dest_def.getData_type());
                                        if (method != null) {
                                            _f_dest_val = method.apply(_f_dest_val, _f_src_val, runtime_dest_def, runtime_src_def, cp);
                                            tmp_dest_fd.field.set(target, _f_dest_val);
                                        } else {
                                            throw new RuntimeException("字段" + tmp_src_fd.field.getName() + "类型不匹配，无法赋值!");
                                        }
                                    }
                                }
                                if (t1 && _f_src_val != null) { // 如果有日期类型
                                    assginValue_DateTime(tmp_src_fd, tmp_dest_fd, target, _f_src_val);
                                } else if (t2 && _f_src_val != null) { // String转基本类型或者封装类型
                                    tmp_dest_fd.field.set(target, BeanUtil.parsePrimitiveOrWrapperOrStringType(_f_src_val, _dest_clazz));
                                } else if (t3 && _f_src_val != null) { // 基本类型或者封装类型转String
                                    tmp_dest_fd.field.set(target, String.valueOf(_f_src_val == null ? "" : _f_src_val));
                                }
                            }
                        }
                    }
                }

            }
        } catch (IllegalAccessException | ClassNotFoundException e) {
            System.out.println(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException(e.getMessage());
        }
        return target;
    }

    /**
     * 集合向集合复制
     * <br/>
     * 在这里我们不支持Collection&lt;Enum&gt; 到Collection&lt;Enum>或者Collection&lt;Enum> 到Collection&lt;String>等之间的复制
     * @param target
     * @param src
     * @param _target_def
     * @param _src_def
     * @param cp
     * @return
     */
    public static Object copyCollection2Collection(Object target, Object src, CopyDefinition _target_def, CopyDefinition _src_def, CopyParam cp) {
        TypeDefinition target_def = (TypeDefinition) _target_def;
        TypeDefinition src_def = (TypeDefinition) _src_def;
        if (target == null || target.getClass() != target_def.runtime_class) {//如果目标对象为空，实例化一个出来
            try {
                target = ClassUtil.newInstance(target_def.runtime_class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        Collection<String> exclude_fields = cp.exclude_fields;
        CopyFeature.CopyFeatureHolder cfh = cp.copyFeature;
        try {
            Class target_clazz = target_def.runtime_class;
            if (target_clazz == null) {
                target_clazz = target.getClass(); // 目标对象类
            }
            Class src_clazz = src_def.runtime_class;
            if (ObjectUtils.isNotEmpty(src)) {
                Collection src_collection = (Collection) src;
                Class _dest_Element_clazz = null, _src_Element_clazz = null;
                if (target_def.isGeneric) {
                    _dest_Element_clazz = target_def.parameter_type_Defines[0].runtime_class;
                    _src_Element_clazz = src_def.parameter_type_Defines[0].runtime_class;
                    if (target_def.parameter_type_Defines[0].isPrimitive) {
                        _dest_Element_clazz = ClassUtils.primitiveToWrapper(_dest_Element_clazz);
                    }
                    if (src_def.parameter_type_Defines[0].isPrimitive) {
                        _src_Element_clazz = ClassUtils.primitiveToWrapper(_src_Element_clazz);
                    }
                }

                Collection dest_collection = (Collection) target;
                // 源字段是字符串，并且目标字段是基本或者封装类型，并且目标字段不是空类型或者布尔类型
                boolean t2 =
                        String.class.isAssignableFrom(_src_Element_clazz)
                                && ClassUtils.isPrimitiveOrWrapper(_dest_Element_clazz)
                                && (!(Void.TYPE.equals(_dest_Element_clazz) || Boolean.TYPE.equals(_dest_Element_clazz)));
                // 目标字段是字符串，并且源字段是基本或者封装类型，并且源字段不是空类型或者布尔类型
                boolean t3 =
                        String.class.isAssignableFrom(_dest_Element_clazz)
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
                            dest_collection.add(Short.valueOf(String.valueOf(_f_src_val)));
                        } else if (Integer.class.isAssignableFrom(_dest_Element_clazz)) {
                            dest_collection.add(Integer.valueOf(String.valueOf(_f_src_val)));
                        } else if (Float.class.isAssignableFrom(_dest_Element_clazz)) {
                            dest_collection.add(Float.valueOf(String.valueOf(_f_src_val)));
                        } else if (Double.class.isAssignableFrom(_dest_Element_clazz)) {
                            dest_collection.add(Double.valueOf(String.valueOf(_f_src_val)));
                        } else if (Long.class.isAssignableFrom(_dest_Element_clazz)) {
                            dest_collection.add(Long.valueOf(String.valueOf(_f_src_val)));
                        }
                    } else if (t3) {
                        dest_collection.add(String.valueOf(_f_src_val == null ? "" : _f_src_val));
                    } else {
                        if(_dest_Element_clazz.isEnum()||_src_Element_clazz.isEnum())
                            continue;
                        // 创建元素对象实例
                        Object target_obj = ClassUtil.newInstance(_dest_Element_clazz);
                        TypeDefinition _dest_element_def = ClassUtil.parseType(_dest_Element_clazz);
                        TypeDefinition _src_element_def = ClassUtil.parseType(_src_Element_clazz);
                        if(_dest_element_def.getData_type()==DataTypeEnum.Object_Class){
                            target_obj=BeanConvertCache.copyObject2Object(target_obj, _f_src_val, _dest_element_def, _src_element_def, cp);
                        }else {
                            PentaFunction<Object, Object, CopyDefinition, CopyDefinition, CopyParam, Object> method = BeanConvertCache.bean_method_table.get(_src_element_def.getData_type(), _dest_element_def.getData_type());
                            if (method != null) {
                                target_obj = method.apply(target_obj, _f_src_val, _dest_element_def, _src_element_def, cp);//这里cp可能会有问题，我会仔细想想
                            }
                        }
                        dest_collection.add(target_obj);
                    }
                }
                return target;
            }
        } catch (IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
        return target;
    }

    public static Object copyPrimitiveOrWrapperOrString2Enum(Object target, Object src, CopyDefinition _target_def, CopyDefinition _src_def, CopyParam cp) {

        if (ObjectUtils.isNotEmpty(src)) {
            FieldDefinition target_def = (FieldDefinition) _target_def;
            FieldDefinition src_def = (FieldDefinition) _src_def;
            try {
                EnumInfo target_enumInfo = target_def.getEnumInfo(src_def.field.getDeclaringClass());
                Class enum_class = target_enumInfo.enum_clazz;

                String target_self_field_name = StringUtils.isNotBlank(target_enumInfo.self_field) ? target_enumInfo.self_field : "name";
                List<? extends Enum> target_enum_instances = EnumUtils.getEnumList(enum_class);
                if (ObjectUtils.isNotEmpty(target_enum_instances)) {
                    Field target_self_field = FieldUtils.getField(enum_class, target_self_field_name, true);
                    if (ObjectUtils.isEmpty(target_self_field)) {
                        throw new RuntimeException("字段上注解[EnumFormat]的属性[self_field]对应的字段名称[" + target_self_field_name + "]不存在,停止复制！");
                    }
                    Enum target_instance = EnumUtil.getInstance(target_enumInfo.enum_clazz, target_self_field_name, src);
                    if (target_instance != null) {
                        if (Enum.class.isAssignableFrom(target_enumInfo.define_clazz)) {
                            return target_instance;
                        } else {
                            String target_to_field_name = StringUtils.isNotBlank(target_enumInfo.to_field) ? target_enumInfo.to_field : "name";
                            return EnumUtil.getValue(target_enumInfo.enum_clazz, target_instance, target_to_field_name);
                        }
                    } else {
                        log.warn("枚举类[" + target_enumInfo.enum_clazz.getName() + "]不存在字段上[" + target_self_field_name + "]值为[" + src + "]的枚举实例。");
                    }
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return target;
    }

    public static Object copyEnum2PrimitiveOrWrapperOrString(Object target, Object src, CopyDefinition _target_def, CopyDefinition _src_def, CopyParam cp) {

        if (ObjectUtils.isNotEmpty(src)) {
            FieldDefinition target_def = (FieldDefinition) _target_def;
            FieldDefinition src_def = (FieldDefinition) _src_def;
            try {
                EnumInfo src_enumInfo = src_def.getEnumInfo(target_def.field.getDeclaringClass());
                Class src_enum_class = src_enumInfo.enum_clazz;
                String src_self_field_name = StringUtils.isNotBlank(src_enumInfo.self_field) ? src_enumInfo.self_field : "name";
                String src_to_field_name = StringUtils.isNotBlank(src_enumInfo.to_field) ? src_enumInfo.to_field : "name";

                EnumInfo target_enumInfo = target_def.getEnumInfo(src_def.field.getDeclaringClass());

                Enum src_instance;
                if (src_enum_class.isInstance(src)) {//如果src是枚举类实例
                    src_instance = (Enum) src;
                } else {
                    src_instance = EnumUtil.getInstance(src_enum_class, src_self_field_name, src);
                    if (src_instance == null) {
                        throw new RuntimeException("枚举类[" + target_enumInfo.enum_clazz.getName() + "]不存在字段上[" + src_self_field_name + "]值为[" + src + "]的枚举实例。");
                    }
                }
                if(target_enumInfo!=null&&StringUtils.isNotBlank(target_enumInfo.from_field)){
                    src_to_field_name= target_enumInfo.from_field;
                }
                Object src_to_field_value = EnumUtil.getValue(src_enum_class, src_instance, src_to_field_name);
                return BeanUtil.parsePrimitiveOrWrapperOrStringType(src_to_field_value, target_def.runtime_class);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return target;
    }
    public static Object copyEnum2Enum(Object target, Object src, CopyDefinition _target_def, CopyDefinition _src_def, CopyParam cp) {

        if (ObjectUtils.isNotEmpty(src)) {
            FieldDefinition target_def = (FieldDefinition) _target_def;
            FieldDefinition src_def = (FieldDefinition) _src_def;
            try {
                EnumInfo src_enumInfo = src_def.getEnumInfo(target_def.field.getDeclaringClass());
                Class src_enum_class = src_enumInfo.enum_clazz;
                String src_self_field_name = StringUtils.isNotBlank(src_enumInfo.self_field) ? src_enumInfo.self_field : "name";

                //源枚举
                Enum src_instance;
                if (src_enum_class.isInstance(src)) {//如果src是枚举类实例
                    src_instance = (Enum) src;
                } else {
                    src_instance = EnumUtil.getInstance(src_enum_class, src_self_field_name, src);
                    if (src_instance == null) {
                        throw new RuntimeException("枚举类[" + src_enumInfo.enum_clazz.getName() + "]不存在字段上[" + src_self_field_name + "]值为[" + src + "]的枚举实例。");
                    }
                }

                EnumInfo target_enumInfo = target_def.getEnumInfo(src_def.field.getDeclaringClass());
                Class target_enum_class = target_enumInfo.enum_clazz;
                String from_field_name=StringUtils.isNotBlank(target_enumInfo.from_field)?target_enumInfo.from_field:StringUtils.isNotBlank(src_enumInfo.to_field)?src_enumInfo.to_field:"name";
                String target_self_field_name=StringUtils.isNotBlank(target_enumInfo.self_field)?target_enumInfo.self_field:"name";

                //目标枚举
                Object from_value=EnumUtil.getValue(src_enum_class,src_instance,from_field_name);
                Enum target_instance=EnumUtil.getInstance(target_enum_class,target_self_field_name,from_value);
                if(target_instance==null){
                    throw new RuntimeException("枚举类[" + target_enumInfo.enum_clazz.getName() + "]不存在字段上[" + target_self_field_name + "]值为[" + String.valueOf(from_value) + "]的枚举实例。");
                }
                if (target_enumInfo.define_clazz==target_enumInfo.enum_clazz) {//如果target是枚举类实例
                   return target_instance;
                } else {
                    return BeanUtil.parsePrimitiveOrWrapperOrStringType(from_value, target_def.runtime_class);
                }

            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return target;
    }


    private static void assginValue_DateTime(
            FieldDefinition src_fd, FieldDefinition dest_fd, Object target, Object src_val)
            throws IllegalAccessException {
        Class<?> _src_clazz = src_fd.runtime_class;
        Class<?> _dest_clazz = dest_fd.runtime_class;
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
