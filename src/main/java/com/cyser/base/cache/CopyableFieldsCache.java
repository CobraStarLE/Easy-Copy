package com.cyser.base.cache;

import com.cyser.base.bean.FieldDefinition;
import com.cyser.base.bean.TypeDefinition;
import com.cyser.base.enums.ClassTypeEnum;
import com.cyser.base.utils.ClassUtil;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Bean中可复制值的字段缓存
 */
@Slf4j
public class CopyableFieldsCache {

    public static Map<String, Map<String, FieldDefinition>> FIELDS_CACHE = Maps.newHashMap();

    /**
     * 返回封装Class中可序列化的字段Map，
     * <br/>
     * 支持"T t","Cat&lt;T&gt;"这样的字段
     * @return Map<String, FieldDefinition>
     * @throws ClassNotFoundException
     */
    public static synchronized Map<String, FieldDefinition> getSerialFieldDefinitions(TypeDefinition type_def)
            throws ClassNotFoundException {
        Class clazz=type_def.runtime_class;
        String cache_key=clazz.getName();//缓存Key
        Map<String,Class> parameter_type_corresponds=type_def.parameter_type_corresponds;
        if((type_def.class_type==ClassTypeEnum.Class&&type_def.isGeneric)||(type_def.class_type==ClassTypeEnum.ParameterizedType)){
            cache_key=clazz.getName();
            for(Map.Entry<String,Class> entry:parameter_type_corresponds.entrySet()){
                cache_key+="#"+entry.getValue().getName();
            }
        }
        Map<String, FieldDefinition> serial_fd_map = FIELDS_CACHE.get(cache_key); // 返回结果Map
        if (ObjectUtils.anyNull(serial_fd_map)) {
            synchronized (CopyableFieldsCache.class) {
                serial_fd_map = FIELDS_CACHE.get(cache_key);
                if (ObjectUtils.anyNull(serial_fd_map)) {
                    Collection<Field> all_dest_fields_list =
                            ClassUtil.getAllFieldsCollection(clazz); // 目标类所有字段
                    if (ObjectUtils.isEmpty(all_dest_fields_list)) {
                        log.warn("当前类[" + clazz.getName() + "]未包含任何字段.");
                    }
                    else {
                        Collection<FieldDefinition> serial_fd_list; // 目标类可序列化字段
                        List<FieldDefinition> all_fd_list = new ArrayList<>(); // 目标类所有字段
                        for (Field field : all_dest_fields_list) {
                            all_fd_list.add(ClassUtil.parseField(field,parameter_type_corresponds));
                        }
                        serial_fd_list =
                                all_fd_list.stream().filter(o -> o.isSerializable).collect(Collectors.toList());
                        if (ObjectUtils.isEmpty(serial_fd_list)) {
                            log.warn("当前类[" + clazz.getName() + "]未包含任何可序列化字段.");
                        }
                        else {
                            serial_fd_map = Maps.uniqueIndex(serial_fd_list, o -> o.field.getName());
                        }
                    }
                    if (ObjectUtils.isEmpty(serial_fd_map)) {
                        serial_fd_map = new HashMap<>();
                    }

                    FIELDS_CACHE.put(cache_key, serial_fd_map);
                }
            }
        }
        return serial_fd_map;
    }
}
