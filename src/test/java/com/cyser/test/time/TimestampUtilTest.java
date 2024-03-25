package com.cyser.test.time;

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

    public static void main(String[] args) throws Exception {
        test4();
    }
}
