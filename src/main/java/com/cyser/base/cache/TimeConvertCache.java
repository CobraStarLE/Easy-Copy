package com.cyser.base.cache;

import com.cyser.base.bean.FieldDefinition;
import com.cyser.base.function.TernaryFunction;
import com.cyser.base.utils.TimestampUtil;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class TimeConvertCache {
    /**
     * 时间转换方法缓存
     */
    public static final Table<
            Class, Class, TernaryFunction<FieldDefinition, FieldDefinition, Object, Object>>
            time_method_table = HashBasedTable.create();

    static {
        // LocalDate.class
        time_method_table.put(
                LocalDate.class,
                LocalDateTime.class,
                (src_fd, dest_fd, src_val) -> TimestampUtil.toLocalDate((LocalDateTime) src_val));
        time_method_table.put(
                LocalDate.class,
                Date.class,
                (src_fd, dest_fd, src_val) -> TimestampUtil.toLocalDate((Date) src_val));
        time_method_table.put(
                LocalDate.class,
                Long.class,
                (src_fd, dest_fd, src_val) -> TimestampUtil.toLocalDate((Long) src_val));
        time_method_table.put(
                LocalDate.class,
                String.class,
                (src_fd, dest_fd, src_val) -> {
                    try {
                        if (!src_fd.isTime)
                            throw new RuntimeException(
                                    "目标对象字段[" + src_fd.field.getName() + "]不是日期字符串,解决方案:在该字段上加上@TimeFormat");
                        return TimestampUtil.toLocalDate((String) src_val, src_fd.timeFormat);
                    }
                    catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                });
        // LocalDateTime.class
        time_method_table.put(
                LocalDateTime.class,
                LocalDate.class,
                (src_fd, dest_fd, src_val) -> TimestampUtil.toLocalDateTime((LocalDate) src_val));
        time_method_table.put(
                LocalDateTime.class,
                Date.class,
                (src_fd, dest_fd, src_val) -> TimestampUtil.toLocalDateTime((Date) src_val));
        time_method_table.put(
                LocalDateTime.class,
                Long.class,
                (src_fd, dest_fd, src_val) -> TimestampUtil.toLocalDateTime((Long) src_val));
        time_method_table.put(
                LocalDateTime.class,
                String.class,
                (src_fd, dest_fd, src_val) -> {
                    try {
                        if (!src_fd.isTime)
                            throw new RuntimeException(
                                    "目标对象字段[" + src_fd.field.getName() + "]不是日期字符串,解决方案:在该字段上加上@TimeFormat");
                        return TimestampUtil.toLocalDateTime((String) src_val, src_fd.timeFormat);
                    }
                    catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                });
        // Date.class
        time_method_table.put(
                Date.class,
                LocalDate.class,
                (src_fd, dest_fd, src_val) -> TimestampUtil.toDate((LocalDate) src_val));
        time_method_table.put(
                Date.class,
                LocalDateTime.class,
                (src_fd, dest_fd, src_val) -> TimestampUtil.toDate((LocalDateTime) src_val));
        time_method_table.put(
                Date.class, Long.class, (src_fd, dest_fd, src_val) -> TimestampUtil.toDate((Long) src_val));
        time_method_table.put(
                Date.class,
                String.class,
                (src_fd, dest_fd, src_val) -> {
                    try {
                        if (!src_fd.isTime)
                            throw new RuntimeException(
                                    "目标对象字段[" + src_fd.field.getName() + "]不是日期字符串,解决方案:在该字段上加上@TimeFormat");
                        return TimestampUtil.toDate((String) src_val, src_fd.timeFormat);
                    }
                    catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                });

        // Long.class
        time_method_table.put(
                Long.class,
                LocalDate.class,
                (src_fd, dest_fd, src_val) -> TimestampUtil.getUnixTimeStamp((LocalDate) src_val));
        time_method_table.put(
                Long.class,
                LocalDateTime.class,
                (src_fd, dest_fd, src_val) -> TimestampUtil.getUnixTimeStamp((LocalDateTime) src_val));
        time_method_table.put(
                Long.class,
                Date.class,
                (src_fd, dest_fd, src_val) -> TimestampUtil.getUnixTimeStamp((Date) src_val));
        time_method_table.put(
                Long.class,
                String.class,
                (src_fd, dest_fd, src_val) -> {
                    try {
                        if (!src_fd.isTime)
                            throw new RuntimeException(
                                    "目标对象字段[" + src_fd.field.getName() + "]不是日期字符串,解决方案:在该字段上加上@TimeFormat");
                        return TimestampUtil.getUnixTimeStamp(
                                (String) src_val, TimestampUtil.TimeMode.CURRENT, src_fd.timeFormat);
                    }
                    catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                });

        // String.class
        time_method_table.put(
                String.class,
                LocalDate.class,
                (src_fd, dest_fd, src_val) -> {
                    if (!dest_fd.isTime)
                        throw new RuntimeException(
                                "目标对象字段[" + dest_fd.field.getName() + "]不是日期字符串,解决方案:在该字段上加上@TimeFormat");
                    return TimestampUtil.format((LocalDate) src_val, dest_fd.timeFormat);
                });
        time_method_table.put(
                String.class,
                LocalDateTime.class,
                (src_fd, dest_fd, src_val) -> {
                    if (!dest_fd.isTime)
                        throw new RuntimeException(
                                "目标对象字段[" + dest_fd.field.getName() + "]不是日期字符串,解决方案:在该字段上加上@TimeFormat");
                    return TimestampUtil.format((LocalDateTime) src_val, dest_fd.timeFormat);
                });
        time_method_table.put(
                String.class,
                Date.class,
                (src_fd, dest_fd, src_val) -> {
                    if (!dest_fd.isTime)
                        throw new RuntimeException(
                                "目标对象字段[" + dest_fd.field.getName() + "]不是日期字符串,解决方案:在该字段上加上@TimeFormat");
                    return TimestampUtil.format((Date) src_val, dest_fd.timeFormat);
                });
        time_method_table.put(
                String.class,
                Long.class,
                (src_fd, dest_fd, src_val) -> {
                    if (!dest_fd.isTime)
                        throw new RuntimeException(
                                "目标对象字段[" + dest_fd.field.getName() + "]不是日期字符串,解决方案:在该字段上加上@TimeFormat");
                    return TimestampUtil.format((Long) src_val, dest_fd.timeFormat);
                });
    }
}
