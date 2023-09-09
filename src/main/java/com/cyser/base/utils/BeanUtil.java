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

        PentaFunction<Object, Object, CopyDefinition, CopyDefinition, CopyParam, Object> copy_opera= BeanConvertCache.bean_method_table.get(src_type_def.getData_type(),dest_type_def.getData_type());
        if (copy_opera != null) {
            target=copy_opera.apply(target,source,dest_type_def,src_type_def,cp);
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

    public static Object parsePrimitiveOrWrapperOrStringType(Object _f_src_val,Class _dest_clazz){
        if (Character.class.isAssignableFrom(_dest_clazz)) {
            if (String.valueOf(_f_src_val).length() > 1)
                throw new RuntimeException("数据长度大于一，无法给char或者Character类型赋值!");
            return Character.valueOf((char)(_f_src_val));
        } else if (Short.class.isAssignableFrom(_dest_clazz)) {
            return Short.valueOf(String.valueOf(_f_src_val));
        } else if (Integer.class.isAssignableFrom(_dest_clazz)) {
            return Integer.valueOf(String.valueOf(_f_src_val));
        } else if (Float.class.isAssignableFrom(_dest_clazz)) {
            return Float.valueOf(String.valueOf(_f_src_val));
        } else if (Double.class.isAssignableFrom(_dest_clazz)) {
            return Double.valueOf(String.valueOf(_f_src_val));
        } else if (Long.class.isAssignableFrom(_dest_clazz)) {
            return Long.valueOf(String.valueOf(_f_src_val));
        }else if(Boolean.class.isAssignableFrom(_dest_clazz)){
            return Boolean.valueOf(String.valueOf(_f_src_val));
        } else if (String.class.isAssignableFrom(_dest_clazz)) {
            return String.valueOf(_f_src_val == null ? "" : _f_src_val);
        }
        return _f_src_val;
    }
}
