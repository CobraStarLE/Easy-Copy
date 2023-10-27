package com.cyser.base.param;

import com.cyser.base.enums.CopyFeature;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

/**
 * 拷贝参数，在BeanUtil.cope时使用，用于在拷贝对象时指定 拷贝的字段或者不要拷贝的字段以及拷贝特色{@link CopyFeature}
 * </br>
 * <pre>
 * subCopyParam:
 *     例如{@code class A{                                  classB{
 *         private Sting name;                           private Sting name;
 *         private List<B> b_list;                       private int age;
 *     }
 *     }
 * </pre>
 */
@Data
public class CopyParamBean {

    /**
     * 用来标识当前CopyParamBean,在当前线程是唯一的
     */
    private String id;

    /**
     * 不需要拷贝的字段
     */
    public Collection<String> exclude_fields;

    /**
     * 拷贝特色，详见{@link CopyFeature}
     */
    public CopyFeature.CopyFeatureHolder copyFeature;

    public CopyParamBean(CopyParamBeanBuilder builder) {
        this.id=builder.id;
        this.exclude_fields=builder.exclude_fields;
        this.copyFeature = builder.copyFeature;
    }

    public CopyParamBean config(CopyFeature feature, boolean state){
        this.copyFeature.configure(feature,state);
        return this;
    }

    public static class CopyParamBeanBuilder{
        private String id;
        public Collection<String> exclude_fields;
        public Table<Object,Object, CopyParamBean> copyParams;

        public CopyFeature.CopyFeatureHolder copyFeature;

        public CopyParamBeanBuilder(){
            this.id=UUID.randomUUID().toString();
            this.exclude_fields= new ArrayList<>();
            this.copyFeature = new CopyFeature.CopyFeatureHolder();
            this.copyParams= HashBasedTable.create();
        }

        public CopyParamBeanBuilder id(String id){
            this.id=id;
            return this;
        }

        public CopyParamBeanBuilder exclude_fields(Collection<String> exclude_fields){
            this.exclude_fields=exclude_fields;
            return this;
        }

        public CopyParamBeanBuilder copyFeature(CopyFeature.CopyFeatureHolder copyFeature){
            this.copyFeature=copyFeature;
            return this;
        }

        public CopyParamBeanBuilder copyParams(Table<Object,Object, CopyParamBean> copyParams){
            this.copyParams=copyParams;
            return this;
        }

        public CopyParamBean build(){
            return new CopyParamBean(this);
        }

    }

}
