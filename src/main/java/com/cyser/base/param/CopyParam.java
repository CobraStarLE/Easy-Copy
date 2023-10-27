package com.cyser.base.param;

import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

@Data
public class CopyParam {

    public CopyParam(){
        this.paramBeanMap=new HashMap<>(8);
    }

    /**
     * 默认的拷贝参数
     */
    public CopyParamBean defaultParam;

    /**
     * 拷贝参数的map
     * key：自定义id，与@CopyParamIdentifier定义id一致，用来获取拷贝参数
     * value：拷贝参数
     */
    public Map<String,CopyParamBean> paramBeanMap;

    public CopyParamBean getDefaultParam() {
        if(ObjectUtils.allNull(defaultParam)){
            defaultParam=new CopyParamBean.CopyParamBeanBuilder().build();
        }
        return defaultParam;
    }
}
