package com.cyser.test;

import com.cyser.base.utils.TimestampUtil;

public class TimestampUtilTest {

    public static void main(String[] args) {
        long unixtime=TimestampUtil.getUnixTimeStamp(2023,11,2,14,2,56);
        String dateStr=TimestampUtil.format(unixtime,TimestampUtil.FastDateFormatPattern.NORM_DATETIME_SLASH_SECOND_PATTERN2);
        System.out.println(dateStr);
    }
}
