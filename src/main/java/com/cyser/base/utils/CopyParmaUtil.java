package com.cyser.base.utils;

import com.cyser.base.param.CopyParam;
import com.cyser.base.param.CopyParamBean;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.Map;

public class CopyParmaUtil {

    private static Cache<String,Map<String, Field>> FIELD_CACHE = CacheBuilder.newBuilder().maximumSize(Long.MAX_VALUE).build();


    private CopyParmaUtil(){}

    /**
     * 根据id获取CopyParamBean
     * @param id 要获取的CopyParamBean对应id
     * @param super_id 如果当前id对应的是字段，则super_id是实体类的CopyParamBean对应id，
     * @param cp 传递的拷贝参数
     * @return 如果当前id对应的CopyParamBean为空，则获取super_id对应的CopyParamBean，否则返回默认CopyParamBean
     */
    public static CopyParamBean getById(String id, String super_id, CopyParam cp){
        CopyParamBean result=null;
        Map<String,CopyParamBean> paramBeanMap=cp.paramBeanMap;
        if(StringUtils.isNotBlank(id)){
            result=paramBeanMap.get(id);
        }
        if(ObjectUtils.allNull(result)){
            result=paramBeanMap.get(super_id);
        }
        if(ObjectUtils.allNull(result)){
            result=cp.defaultParam;
        }
        return result;
    }
}
