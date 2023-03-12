package com.cyser.base.enums;

import org.apache.commons.lang3.ObjectUtils;

public enum CopyFeature {

    COPY_NULL_VALUE,//当源对象字段为null时拷贝空值
    FORCE_OVERWRITE;//当目标对象字段有值时强制覆盖

    public int mask = 0;//掩码

    CopyFeature() {
        mask = (1 << ordinal());
    }

    public static class CopyFeatureHolder {
        /**
         * 是否启用
         *
         * @param features
         * @param feature
         * @return
         */
        public static boolean isEnabled(int features, CopyFeature feature) {
            return (features & feature.mask) != 0;
        }

        /**
         * 计算features结果
         *
         * @param features
         * @return
         */
        public static int calculateFeatures(CopyFeature[] features) {
            int features_value = 0;
            if (ObjectUtils.isNotEmpty(features)) {
                for (CopyFeature feature : features) {
                    features_value |= feature.mask;
                }
            }
            return features_value;
        }
    }
}
