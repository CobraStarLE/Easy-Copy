package com.cyser.test.time;

import com.cyser.base.enums.FastDateFormatPattern;
import com.cyser.base.enums.TimeMode;
import com.cyser.base.utils.TimestampUtil;

import java.time.LocalDateTime;
import java.util.Date;

public class TimestampUtilTest {


    public static void test() throws Exception {
        LocalDateTime ldt = TimestampUtil.getLocalDateTime(TimeMode.TWENTY_THREE);
        System.out.println();
    }

    public static void test2() {
        Date date = new Date();
        Date date_new = TimestampUtil.plusYear(date, -10);
        System.out.println();
    }

    public static void test3() {
        Date date = new Date();
        Date date_new = TimestampUtil.plusMonth(date, -4);
        System.out.println();
    }

    public static void test4() {
        Date date = new Date();
        Date date_new = TimestampUtil.plusSecond(date, 10);
        System.out.println();
    }

    public static void test5() {
        Date date = TimestampUtil.getStartOfMonth(new Date(),TimeMode.ZERO_ZERO);
        System.out.println();
    }

    public static void test6() {
        long now =TimestampUtil.getUnixTimeStamp();
        long date = TimestampUtil.getStartOfMonth(now,TimeMode.ZERO_ZERO);
        String str = TimestampUtil.format(date, FastDateFormatPattern.ISO_DATETIME_NO_T_FORMAT);
        System.out.println(str);
    }

    public static void test7(){
        long now =TimestampUtil.getUnixTimeStamp(TimeMode.ZERO_ZERO);
        long week_ago = TimestampUtil.plusDay(now,-6);
        String str = TimestampUtil.format(week_ago, FastDateFormatPattern.ISO_DATETIME_NO_T_FORMAT);
        System.out.println(str);
    }

    public static void test8(){
        long now = TimestampUtil.getUnixTimeStamp();
        long lastMonth =TimestampUtil.plusMonth(now, -1);
        String str = TimestampUtil.format(lastMonth, FastDateFormatPattern.ISO_DATETIME_NO_T_FORMAT);
        System.out.println(str);
    }

    public static void test9(){
        long now = TimestampUtil.getUnixTimeStamp();
        long new_now =TimestampUtil.getUnixTimeStamp(now,TimeMode.ZERO_ZERO);
        String str = TimestampUtil.format(new_now, FastDateFormatPattern.ISO_DATETIME_NO_T_FORMAT);
        System.out.println(str);
    }

    public static void test10(){
        long now = TimestampUtil.getUnixTimeStamp();
        long start=TimestampUtil.getStartOfHour();
        String str = TimestampUtil.format(start, FastDateFormatPattern.ISO_DATETIME_NO_T_FORMAT);
        System.out.println(str);

    }
    public static void test11(){
        long start_of_hour=TimestampUtil.getStartOfHour();// 当前小时0分0秒
        long next_hour_zero=TimestampUtil.plusHour(start_of_hour,1);
        long last_day_zero=TimestampUtil.plusDay(next_hour_zero,-1);

        String str = TimestampUtil.format(last_day_zero, FastDateFormatPattern.ISO_DATETIME_NO_T_FORMAT);
        System.out.println(str);

    }

    public static void test12(){
        long now = TimestampUtil.getUnixTimeStamp();
        long today_first =TimestampUtil.getUnixTimeStamp(TimeMode.ZERO_ZERO);
        long serven_day_ago =TimestampUtil.plusDay(today_first, -6);

        String str = TimestampUtil.format(serven_day_ago, FastDateFormatPattern.ISO_DATETIME_NO_T_FORMAT);
        System.out.println(str);
    }
    public static void test13() {
        long today_start =TimestampUtil.getUnixTimeStamp(TimeMode.ZERO_ZERO);
        long next_day_start =TimestampUtil.plusDay(today_start, 1);

        long serven_day_ago =TimestampUtil.plusDay(next_day_start, -7);

        String str = TimestampUtil.format(serven_day_ago, FastDateFormatPattern.ISO_DATETIME_NO_T_FORMAT);
        System.out.println(str);
    }
    public static void main(String[] args) throws Exception {
        test12();
        test13();
    }
}
