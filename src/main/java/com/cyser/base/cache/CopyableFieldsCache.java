package com.cyser.base.cache;

import com.cyser.base.bean.ClassDefinition;
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

    public static Map<String, FieldDefinition> getSerialFieldDefinitions(TypeDefinition clazz_def) throws ClassNotFoundException {
        Class clazz=clazz_def.rawClass;
        if(clazz_def.type== ClassTypeEnum.Class){
            return getSerialFieldDefinitions(clazz);
        }else if(clazz_def.type==ClassTypeEnum.ParameterizedType){
//            return getSerialFieldDefinitions(clazz,clazz_def.parameterType.rawClass);
            return null;
        }else{
            return null;
        }
    }

    /**
     * 返回某个Class中可序列化的字段Map
     *
     * @param clazz
     * @return Map<String, FieldDefinition>
     * @throws ClassNotFoundException
     */
    public static Map<String, FieldDefinition> getSerialFieldDefinitions(Class clazz)
            throws ClassNotFoundException {
        Map<String, FieldDefinition> serial_fd_map = FIELDS_CACHE.get(clazz.getName()); // 返回结果Map
        if (ObjectUtils.anyNull(serial_fd_map)) {
            synchronized (CopyableFieldsCache.class) {
                serial_fd_map = FIELDS_CACHE.get(clazz.getName());
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
                            all_fd_list.add(ClassUtil.parseField(field));
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

                    FIELDS_CACHE.put(clazz.getName(), serial_fd_map);
                }
            }
        }
        return serial_fd_map;
    }

    /**
     * 返回封装Class中可序列化的字段Map
     *
     * @param clazz                             封装类
     * @param parameter_clazz                   封装类型,这里的类型只针对"T t"这样的字段，不支持 "Cat&lt;T&gt;"这样的字段
     * @return Map<String, FieldDefinition>
     * @throws ClassNotFoundException
     */
    public static Map<String, FieldDefinition> getSerialFieldDefinitions(Class clazz,Class parameter_clazz)
            throws ClassNotFoundException {
        //todo 解析封装类型的类
        Map<String, FieldDefinition> serial_fd_map = FIELDS_CACHE.get(clazz.getName()+"_"+parameter_clazz.getName()); // 返回结果Map
        if (ObjectUtils.anyNull(serial_fd_map)) {
            synchronized (CopyableFieldsCache.class) {
                serial_fd_map = FIELDS_CACHE.get(clazz.getName());
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
                            all_fd_list.add(ClassUtil.parseField(field));
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

                    FIELDS_CACHE.put(clazz.getName(), serial_fd_map);
                }
            }
        }
        return serial_fd_map;
    }
}
