package com.cyser.base.param;

import com.cyser.base.enums.CopyFeature;
import lombok.Data;

import java.util.Collection;
import java.util.Map;

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
public class CopyParam {

    /**
     * 不需要拷贝的字段
     */
    public Collection<String> exclude_fields;

    /**
     * 拷贝特色，详见{@link CopyFeature}
     */
    public CopyFeature.CopyFeatureHolder copyFeature;

    /**
     * 嵌套对象的拷贝参数，例如：
     *  <pre>
     *  {@code class A{                                 classB{
     *       private Sting name;                           private Sting name;
     *       private List<B> b_list;                       private int age;
     *   }
     *  我们在为A对象拷贝b_list时，同样可以指定拷贝B类时需要的拷贝参数，这时候我们可以为subCopyParam设定key为“b_list”，值为 CopyParam对象，
     *  如果B也有List<C> c_list,那我们也可以在拷贝c_list时指定拷贝参数，这时候我们可以subCopyParam设定key为“b_list.c_list”，值为 CopyParam对象,
     *  以此类推...
     *
     * </pre>
     */
    public Map<String,CopyParam> subCopyParam;

    public CopyParam() {
        this.copyFeature = new CopyFeature.CopyFeatureHolder();
    }
}
