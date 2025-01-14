package com.cyser.test;

import com.cyser.base.enums.FastDateFormatPattern;
import com.cyser.base.utils.TimestampUtil;

public class TimestampUtilTest {

    public static void main(String[] args) {
//        long unixtime= TimestampUtil.getUnixTimeStamp(2023,12,6,0,0,0);
//        long unixtime2=TimestampUtil.getUnixTimeStamp(2023,12,6,1,0,0);
//        String dateStr=TimestampUtil.format(unixtime, FastDateFormatPattern.NORM_DATETIME_SLASH_SECOND_PATTERN2);
//        System.out.println(dateStr);
//        System.out.println(unixtime);
//        System.out.println(unixtime2);

        boolean a=TimestampUtil.duringCurrDay(1736820956104L);
        System.out.println();
    }
}
