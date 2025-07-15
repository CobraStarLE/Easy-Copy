package com.cyser.base.bean;

import lombok.Data;

/**
 * 封装时间信息的实体类
 */
@Data
public class TimeInfo {
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int second;
    private int millisecond;
    private int microsecond;
    private int nanosecond;
}