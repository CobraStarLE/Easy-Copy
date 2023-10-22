package com.cyser.base.enums;

public enum CopyFeature {
    COPY_NULL_VALUE(true), // 当为true时，源对象字段为null时拷贝空值null
    FORCE_OVERWRITE(true), // 当为true时，目标对象字段有值时强制覆盖
    CASE_SENSITIVE(true); // 当为true时，字段名称区分大小写

    private final boolean _defaultState;//默认状态（是否启用：true，启用；false：不启用）
    private final int _mask;//掩码

    CopyFeature(boolean defaultState) {
        _defaultState = defaultState;
        _mask = (1 << ordinal());
    }

    public boolean enabledByDefault() { return _defaultState; }
    public boolean enabledIn(int flags) { return (flags & getMask()) != 0; }
    public int getMask() { return (1 << ordinal()); }

    public static int collectDefaults() {
        int flags = 0;
        for (CopyFeature f : values()) {
            if (f.enabledByDefault()) { flags |= f.getMask(); }
        }
        return flags;
    }

    public static class CopyFeatureHolder {

        protected final static int DEFAULT_FEATURE_FLAGS = CopyFeature.collectDefaults();

        protected int _copyFeatures = DEFAULT_FEATURE_FLAGS;


        /**
         * 配置
         * @param f
         * @param state
         * @return
         */
        public final CopyFeatureHolder configure(CopyFeature f, boolean state) {
            return state ? enable(f) : disable(f);
        }

        /**
         * 启用
         * @param f
         * @return
         */
        public CopyFeatureHolder enable(CopyFeature f) {
            _copyFeatures |= f.getMask();
            return this;
        }

        /**
         * 禁用
         * @param f
         * @return
         */
        public CopyFeatureHolder disable(CopyFeature f) {
            _copyFeatures &= ~f.getMask();
            return this;
        }

        /**
         * 是否启用
         *
         * @param feature
         * @return
         */
        public boolean isEnabled(CopyFeature feature) {
            return (_copyFeatures & feature._mask) != 0;
        }

    }
}
