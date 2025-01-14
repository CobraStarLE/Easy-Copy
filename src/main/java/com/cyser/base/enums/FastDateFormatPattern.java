package com.cyser.base.enums;

public enum FastDateFormatPattern {
        /**
         * 11位数字时间戳，精确到毫秒
         */
        PURE_UNIX_TIME_FORMAT("1111111111111"),

        /**
         * ISO带T的日期时间格式，精确到秒
         */
        ISO_DATETIME_FORMAT("yyyy-MM-dd'T'HH:mm:ss"),

        /**
         * ISO带T的日期时间格式，精确到毫秒
         */
        ISO_DATETIME_ZONE_FORMAT("yyyy-MM-dd'T'HH:mm:ssXXX"),

        /**
         * ISO非带T的日期时间格式，精确到秒
         */
        ISO_DATETIME_NO_T_FORMAT("yyyy-MM-dd HH:mm:ss"),

        /**
         * ISO非带T的日期时间格式，精确到毫秒
         */
        ISO_DATETIME_ZONE_NO_T_FORMAT("yyyy-MM-dd HH:mm:ssXXX"),

        /**
         * ISO带T指定时区的日期时间格式，精确到秒
         */
        ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT_SECONDS("yyyy-MM-dd'T'HH:mm:ssZZ"),

        /**
         * ISO非带T指定时区的日期时间格式，精确到秒
         */
        ISO_8601_EXTENDED_DATETIME_NO_T_TIME_ZONE_FORMAT_SECONDS("yyyy-MM-dd HH:mm:ssZZ"),

        /**
         * ISO带T指定时区的日期时间格式，精确到毫秒
         */
        ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT_MILLISECONDS("yyyy-MM-dd'T'HH:mm:ss.SSSZZ"),

        /**
         * ISO非带T指定时区的日期时间格式，精确到毫秒
         */
        ISO_8601_EXTENDED_DATETIME_NO_T_TIME_ZONE_FORMAT_MILLISECONDS("yyyy-MM-dd HH:mm:ss.SSSZZ"),

        /**
         * ISO日期格式，精确到天
         */
        ISO_DATE_FORMAT("yyyy-MM-dd"),

        /**
         * ISO带T的时间格式，精确到秒
         */
        ISO_TIME_FORMAT("'T'HH:mm:ss"),

        /**
         * ISO带T指定时区的时间格式，精确到秒
         */
        ISO_TIME_TIME_ZONE_FORMAT("'T'HH:mm:ssZZ"),

        /**
         * ISO非带T的时间格式，精确到秒
         */
        ISO_TIME_NO_T_FORMAT("HH:mm:ss"),

        /**
         * ISO非带T指定时区的时间格式，精确到秒
         */
        ISO_TIME_NO_T_TIME_ZONE_FORMAT("HH:mm:ssZZ"),

        /**
         * 通用分隔符为横杠“-”的日期时间格式，精确到分钟
         */
        NORM_DATETIME_HENGSU_MINUTE_PATTERN("yyyy-MM-dd HH:mm"),

        /**
         * 通用分隔符为斜杠“/”的日期时间格式，精确到分钟
         */
        NORM_DATETIME_SLASH_MINUTE_PATTERN("yyyy/MM/dd HH:mm"),

        /**
         * 通用分隔符为斜杠“/”的日期时间格式，精确到秒
         */
        NORM_DATETIME_SLASH_SECOND_PATTERN("yyyy/MM/dd HH:mm:ss"),

        /**
         * 通用分隔符为斜杠“/”的日期时间格式，精确到秒
         */
        NORM_DATETIME_SLASH_SECOND_PATTERN2("yyyy/M/d H:m:s"),

        /**
         * 纯日期时间格式，精确到秒
         */
        PURE_DATETIME_PATTERN("yyyyMMddHHmmss"),

        /**
         * 纯日期格式
         */
        PURE_DATE_FORMAT("yyyyMMdd"),

        /**
         * 中国式日期时间格式
         */
        CN_DATE_FORMAT("yyyy年MM月dd日");

        private String formatPattern;

        FastDateFormatPattern(String formatPattern) {
            this.formatPattern = formatPattern;
        }

        public String getFormatPattern() {
            return formatPattern;
        }
    }