package com.cyser.base;

import com.cyser.base.enums.CopyFeature;
import com.cyser.base.utils.BeanUtil;

import java.util.Collection;
import java.util.function.Consumer;

public interface Copyable<T> {

    default T copy(Object[] source, CopyFeature.CopyFeatureHolder... cfh) {
        return copy(source, (Collection<String>) null, cfh);
    }

    default T copy(Object[] source, Collection<String> exclude_fields, CopyFeature.CopyFeatureHolder... cfh) {
        BeanUtil.copy(this, source, exclude_fields, cfh);
        return (T) this;
    }

    default T copy(Object[] source, Consumer<T> append, CopyFeature.CopyFeatureHolder... cfh) {
        T t = this.copy(source, cfh);
        append.accept(t);
        return t;
    }

    default T copy(Object[] source, Consumer<T> append, Collection<String> exclude_fields, CopyFeature.CopyFeatureHolder... cfh) {
        T t = this.copy(source, exclude_fields, cfh);
        append.accept(t);
        return t;
    }
}
