package com.cyser.base.utils;

import com.cyser.base.bean.CopyDefinition;
import com.cyser.base.bean.FieldDefinition;
import com.cyser.base.bean.TypeDefinition;
import com.cyser.base.cache.BeanConvertCache;
import com.cyser.base.cache.TimeConvertCache;
import com.cyser.base.enums.ClassTypeEnum;
import com.cyser.base.enums.CopyFeature;
import com.cyser.base.enums.DataTypeEnum;
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

    public static Object copy(Object target, Object source, CopyParam... cp) {
        if(ObjectUtils.isNotEmpty(target)&&ObjectUtils.isNotEmpty(source)){
            return copy(target, source, target.getClass(), source.getClass(), cp);
        }else{
            log.warn("请检查参数是否为空，停止复制！");
            return target;
        }
    }

    private static Object copy(Object target, Object source, Class dest_clazz, Class src_clazz, CopyParam... cp) {
        if (ObjectUtils.isEmpty(source)) {
            log.warn("被复制对象为空，停止复制。");
            return target;
        }

        if(ObjectUtils.isEmpty(target)&&ObjectUtils.isEmpty(dest_clazz)){
            throw new RuntimeException("参数target和dest_clazz不能同时为空，停止复制！");
        }

        CopyParam _cp;
        if (ObjectUtils.isNotEmpty(cp)) {
            _cp = cp[0];
        } else {
            _cp = new CopyParam();
        }

        try {
            TypeDefinition target_def=ClassUtil.parseType(target.getClass());
            TypeDefinition src_def=ClassUtil.parseType(source.getClass());

            DataTypeEnum target_data_type=target_def.getData_type();
            DataTypeEnum src_data_type=src_def.getData_type();
            if (!(target_data_type==DataTypeEnum.Entity_Class&&src_data_type==DataTypeEnum.Entity_Class)) {
                throw new RuntimeException("该方法只支持实体类（不包括带范型的实体类）复制,停止复制值！请尝试调用[void copy(Object target, Object source, TypeReference dest_tr, TypeReference src_tr, CopyParam ... cp)]方法。");
            }
            if (!target_def.isSerializable) {
                throw new RuntimeException("检查类[" + dest_clazz.getName() + "]是否是final、static、abstract,停止复制值！");
            }
            if (!src_def.isSerializable) {
                throw new RuntimeException("检查类[" + src_clazz.getName() + "]是否是final、static、abstract,停止复制值！");
            }
            return BeanConvertCache.copyEntity2Entity(target,source,target_def,src_def,_cp);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    public static Object copy(Object target, Object source, TypeReference dest_tr, TypeReference src_tr, CopyParam... cp) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (!ObjectUtils.allNotNull(dest_tr, src_tr)) {
            throw new RuntimeException("参数为空,停止复制值！");
        }
        CopyParam _cp;
        if (ObjectUtils.isNotEmpty(cp)) {
            _cp = cp[0];
        } else {
            _cp = new CopyParam();
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
            target=copy_opera.apply(target,source,dest_type_def,src_type_def,_cp);
        }

        return target;
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
