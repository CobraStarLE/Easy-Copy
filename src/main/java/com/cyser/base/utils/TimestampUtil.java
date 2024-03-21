package com.cyser.base.utils;

import com.cyser.base.enums.FastDateFormatPattern;
import com.cyser.base.enums.TimeMode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@Slf4j
public class TimestampUtil {

    private TimestampUtil() {
    }

    public static Long getUnixTimeStamp(TimeMode... timeMode) {
        if (ObjectUtils.isNotEmpty(timeMode)) {
            TimeMode _timeMode = timeMode[0];
            Calendar calendar = getCalendar(_timeMode);
            return getUnixTimeStamp(calendar);
        } else {
            return Instant.now().toEpochMilli();
        }
    }

    /**
     * 得到Unix时间戳，精确到毫秒
     *
     * @return
     */
    public static Long getUnixTimeStamp(LocalDateTime localDateTime, ZoneId... zoneId) {
        ZonedDateTime zdt = getZoneDateTime(localDateTime, zoneId);
        return getUnixTimeStamp(zdt);
    }

    public static Long getUnixTimeStamp(ZonedDateTime zdt) {
        return zdt.toInstant().toEpochMilli();
    }

    public static Long getUnixTimeStamp(Date date, ZoneId... zoneId) {
        ZonedDateTime zdt = getZoneDateTime(date, zoneId);
        return getUnixTimeStamp(zdt);
    }

    public static Long getUnixTimeStamp(LocalDate ld, ZoneId... zoneIds) {
        return getUnixTimeStamp(ld, 0, 0, 0, zoneIds);
    }

    public static Long getUnixTimeStamp(
            LocalDate ld, int hour, int minute, int second, ZoneId... zoneIds) {
        LocalDateTime localDateTime = getLocalDateTime(ld, hour, minute, second);
        return getUnixTimeStamp(localDateTime, zoneIds);
    }

    public static Long getUnixTimeStamp(
            int year, int month, int day, int hour, int minute, int second, ZoneId... zoneIds) {
        ZonedDateTime zdt = getZoneDateTime(year, month, day, hour, minute, second, zoneIds);
        return getUnixTimeStamp(zdt);
    }

    public static Long getUnixTimeStamp(
            int year, int month, int day, TimeMode timeMode, ZoneId... zoneIds) {
        LocalDateTime ldt = getLocalDateTime(year, month, day, timeMode, zoneIds);
        return getUnixTimeStamp(ldt);
    }

    public static Long getUnixTimeStamp(Calendar calendar, ZoneId... zoneId) {
        ZonedDateTime zdt = getZoneDateTime(calendar, zoneId);
        return getUnixTimeStamp(zdt);
    }

    public static Long getUnixTimeStamp(
            String formatTimeStr,FastDateFormatPattern pattern,ZoneId... zoneId)
            throws ParseException {
        LocalDateTime ldt = getLocalDateTime(formatTimeStr,pattern, zoneId);
        return getUnixTimeStamp(ldt,zoneId);
    }

    public static LocalDateTime getLocalDateTime(TimeMode... timeMode) {
        Long unixTime=getUnixTimeStamp(timeMode);
        return getLocalDateTime(unixTime);
    }

    public static LocalDateTime getLocalDateTime(Instant instant, ZoneId... zoneId) {
        ZoneId _zoneId = getZoneId(zoneId);
        return instant.atZone(_zoneId).toLocalDateTime();
    }

    public static LocalDateTime getLocalDateTime(long unixTime, ZoneId... zoneId) {
        Instant instant = Instant.ofEpochMilli(unixTime);
        ZoneId _zoneId = getZoneId(zoneId);
        return LocalDateTime.ofInstant(instant, _zoneId);
    }


    public static LocalDateTime getLocalDateTime(Date date, ZoneId... zoneId) {
        ZoneId _zoneId = getZoneId(zoneId);
        ZonedDateTime zdt = date.toInstant().atZone(_zoneId);
        return zdt.toLocalDateTime();
    }

    public static LocalDateTime getLocalDateTime(LocalDate ld, int hour, int minute, int second) {
        return LocalDateTime.of(ld, LocalTime.of(hour, minute, second));
    }

    public static LocalDateTime getLocalDateTime(LocalDate ld, TimeMode timeMode,ZoneId... zoneId) {
        LocalTime localTime=getLocalTime(timeMode, zoneId);
        return LocalDateTime.of(ld, localTime);
    }


    public static LocalDateTime getLocalDateTime(
            int year, int month, int day, TimeMode timeMode, ZoneId... zoneIds) {
        Calendar calendar = getCalendar(year, month, day, timeMode, zoneIds);
        return getLocalDateTime(calendar, zoneIds);
    }

    public static LocalDateTime getLocalDateTime(Calendar calendar, ZoneId... zoneId) {
        ZoneId _zoneId = getZoneId(zoneId);
        return calendar.getTime().toInstant().atZone(_zoneId).toLocalDateTime();
    }

    public static LocalDateTime getLocalDateTime(
            int year, int month, int day, int hour, int minute, int second, ZoneId... zoneIds) {
        ZoneId zoneId = getZoneId(zoneIds);
        Calendar calendar = getCalendar(year, month, day, hour, minute, second, zoneIds);
        return getLocalDateTime(calendar, zoneId);
    }

    public static LocalDateTime getLocalDateTime(ZoneId ...zoneId){
        ZonedDateTime zdt = getZoneDateTime(zoneId);
        return getLocalDateTime(zdt);
    }


    public static LocalDateTime getLocalDateTime(ZonedDateTime zdt){
        return zdt.toLocalDateTime();
    }

    public static LocalDateTime getLocalDateTime(
            String formatTimeStr,FastDateFormatPattern pattern,ZoneId... zoneId)
            throws ParseException {
        if(StringUtils.isBlank(formatTimeStr)){
            log.warn("要转换的时间格式字符串为空，停止转换。");
            return null;
        }
        LocalDateTime ldt;
        DateTimeFormatter df = DateTimeFormatter.ofPattern(pattern.getFormatPattern());
        // 当格式为年月日时，特殊处理为年月日时分秒格式
        switch (pattern) {
            case PURE_UNIX_TIME_FORMAT:
                ldt = getLocalDateTime(Long.valueOf(formatTimeStr), zoneId);
                break;
            case ISO_DATE_FORMAT:
            case PURE_DATE_FORMAT:
            case CN_DATE_FORMAT:
                LocalDate localDate = LocalDate.parse(formatTimeStr, df);
                ldt = localDate.atStartOfDay();
                break;
            case ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT:
            case ISO_8601_EXTENDED_DATETIME_NO_T_TIME_ZONE_FORMAT:
                Date date = DateUtils.parseDateStrictly(formatTimeStr, pattern.getFormatPattern());
                return getLocalDateTime(date.getTime(), zoneId);
            default:
                ldt = LocalDateTime.parse(formatTimeStr, df);
                break;
        }
        return ldt;
    }

    public static LocalTime getLocalTime(TimeMode timeMode, ZoneId... zoneId) {
        ZoneId _zoneId = getZoneId(zoneId);
        switch (timeMode) {
            case ZERO_ZERO:
                return LocalTime.of(0, 0, 0);
            case TWENTY_THREE:
                return LocalTime.of(23, 59, 59);
            default:
                return LocalTime.now(_zoneId);
        }
    }

    public static ZonedDateTime getZoneDateTime(LocalDateTime localDateTime, ZoneId... zoneId) {
        ZoneId _zoneId = getZoneId(zoneId);
        return localDateTime.atZone(_zoneId);
    }

    public static ZonedDateTime getZoneDateTime( ZoneId... zoneId) {
        Long unixTime=getUnixTimeStamp();
        return getZoneDateTime(unixTime,zoneId);
    }

    public static ZonedDateTime getZoneDateTime(TimeMode timeMode, ZoneId... zoneId) {
        Long unixTime=getUnixTimeStamp(timeMode);
        return getZoneDateTime(unixTime,zoneId);
    }

    public static ZonedDateTime getZoneDateTime(long unixTime, ZoneId... zoneId) {
        ZoneId _zoneId = getZoneId(zoneId);
        Instant instant = Instant.ofEpochMilli(unixTime);
        return instant.atZone(_zoneId);
    }

    public static ZonedDateTime getZoneDateTime(Date date, ZoneId... zoneId) {
        ZoneId _zoneId = getZoneId(zoneId);
        return date.toInstant().atZone(_zoneId);
    }

    public static ZonedDateTime getZoneDateTime(Calendar calendar, ZoneId... zoneId) {
        ZoneId _zoneId = getZoneId(zoneId);
        return calendar.getTime().toInstant().atZone(_zoneId);
    }

    public static ZonedDateTime getZoneDateTime(
            int year, int month, int day, int hour, int minute, int second, ZoneId... zoneId) {
        ZoneId _zoneId = getZoneId(zoneId);
        Calendar calendar = getCalendar(year, month, day, hour, minute, second, _zoneId);
        return getZoneDateTime(calendar, _zoneId);
    }

    public static Calendar getCalendar(ZoneId... zoneId) {
        ZoneId _zoneId = getZoneId(zoneId);
        return Calendar.getInstance(TimeZone.getTimeZone(_zoneId));
    }

    public static Calendar getCalendar(LocalDateTime localDateTime, ZoneId... zoneId) {
       Date date = getDate(localDateTime, zoneId);
       return getCalendar(date, zoneId);
    }

    public static Calendar getCalendar(long unixTime, ZoneId... zoneId) {
        Date date = getDate(unixTime);
        ZoneId _zoneId = getZoneId(zoneId);
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(_zoneId));
        cal.setTime(date);
        return cal;
    }

    public static Calendar getCalendar(TimeMode timeMode, ZoneId... zoneId) {
        ZoneId _zoneId = getZoneId(zoneId);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(_zoneId));
        setHHmmss(calendar, timeMode);
        return calendar;
    }

    public static Calendar getCalendar(
            int year, int month, int day, int hour, int minute, int second, ZoneId... zoneIds) {
        ZoneId zoneId = getZoneId(zoneIds);
        Calendar calendar = getCalendar(zoneId);
        setYYYYMMDD(calendar, year, month, day);
        setHHmmss(calendar, hour, minute, second);
        return calendar;
    }

    public static Calendar getCalendar(Date date, ZoneId... zoneId) {
        ZoneId _zoneId = getZoneId(zoneId);
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(_zoneId));
        cal.setTime(date);
        return cal;
    }

    public static Calendar getCalendar(
            int year, int month, int day, TimeMode timeMode, ZoneId... zoneIds) {
        ZoneId zoneId = getZoneId(zoneIds);
        Calendar calendar = getCalendar(zoneId);
        setYYYYMMDD(calendar, year, month, day);
        setHHmmss(calendar, timeMode);
        return calendar;
    }

    public static Calendar getCalendar(
            LocalDate ld, TimeMode timeMode, ZoneId... zoneIds) {
        ZoneId zoneId = getZoneId(zoneIds);
        Calendar calendar = getCalendar(zoneId);
        setYYYYMMDD(calendar, ld.getYear(), ld.getMonthValue(), ld.getDayOfMonth());
        setHHmmss(calendar, timeMode);
        return calendar;
    }


    public static Date getDate(ZoneId ...zoneId){
        if(ObjectUtils.isEmpty(zoneId)){
            return new Date();
        }
        Calendar calendar=getCalendar(zoneId);
        return getDate(calendar);
    }

    public static Date getDate(long unixTime){
        return new Date(unixTime);
    }

    public static Date getDate(TimeMode timeMode, ZoneId... zoneId){
        Calendar calendar=getCalendar(timeMode,zoneId);
        return getDate(calendar);
    }


    public static Date getDate(LocalDateTime ldt, ZoneId... zoneId){
        ZonedDateTime zdt = getZoneDateTime(ldt,zoneId);
        return getDate(zdt);
    }

    public static Date getDate(ZonedDateTime zdt){
        return Date.from(zdt.toInstant());
    }

    public static Date getDate(Calendar calendar){
        return calendar.getTime();
    }

    public static Date getDate(
            int year, int month, int day, int hour, int minute, int second, ZoneId... zoneIds) {
        Calendar calendar = getCalendar(year, month, day, hour, minute, second, zoneIds);
        return getDate(calendar);
    }

    public static Date getDate(
            int year, int month, int day, TimeMode timeMode, ZoneId... zoneIds) {
        Calendar calendar = getCalendar(year, month, day, timeMode, zoneIds);
        return getDate(calendar);
    }

    public static Date getDate(
            LocalDate ld, TimeMode timeMode, ZoneId... zoneIds) {
        Calendar calendar = getCalendar(ld, timeMode, zoneIds);
        return getDate(calendar);
    }

    public static Date getDate(
            String formatTimeStr,FastDateFormatPattern pattern,ZoneId... zoneId)
            throws ParseException {
        LocalDateTime ldt = getLocalDateTime(formatTimeStr,pattern, zoneId);
        return getDate(ldt,zoneId);
    }


    public static LocalDate getLocalDate(ZoneId ...zoneId){
        ZonedDateTime zdt=getZoneDateTime(zoneId);
        return getLocalDate(zdt);
    }

    public static LocalDate getLocalDate(long unixTime,ZoneId ... zoneId){
        ZonedDateTime zdt = getZoneDateTime(unixTime, zoneId);
        return getLocalDate(zdt);
    }

    public static LocalDate getLocalDate(ZonedDateTime zdt){
        return zdt.toLocalDate();
    }

    public static LocalDate getLocalDate(LocalDateTime ldt, ZoneId... zoneId){
        ZonedDateTime zdt = getZoneDateTime(ldt,zoneId);
        return getLocalDate(zdt);
    }

    public static LocalDate getLocalDate(Calendar calendar, ZoneId... zoneId){
        ZonedDateTime zdt = getZoneDateTime(calendar,zoneId);
        return getLocalDate(zdt);
    }

    public static LocalDate getLocalDate(
            int year, int month, int day, ZoneId... zoneId) {
        ZonedDateTime zdt = getZoneDateTime(year,month,day,0,0,0,zoneId);
        return getLocalDate(zdt);
    }

    public static LocalDate getLocalDate(Date date, ZoneId... zoneId) {
        Instant instant = getInstant(date);
        ZoneId _zoneId = getZoneId(zoneId);
        LocalDate ld = instant.atZone(_zoneId).toLocalDate();
        return ld;
    }

    public static LocalDate getLocalDate(
            String formatTimeStr,FastDateFormatPattern pattern,ZoneId... zoneId)
            throws ParseException {
        LocalDateTime ldt = getLocalDateTime(formatTimeStr,pattern, zoneId);
        return getLocalDate(ldt,zoneId);
    }

    public static Instant getInstant(long unixTime){
        return Instant.ofEpochMilli(unixTime);
    }

    public static Instant getInstant(Date date){
        return date.toInstant();
    }

    public static Instant getInstant(LocalDateTime ldt,ZoneId... zoneId){
        ZoneId _zoneId = getZoneId(zoneId);
        ZoneOffset zoneOffset = OffsetDateTime.now(_zoneId).getOffset();
        return ldt.toInstant(zoneOffset);
    }

    public static Instant getInstant(
            String formatTimeStr,FastDateFormatPattern pattern,ZoneId... zoneId) throws ParseException {
        LocalDateTime ldt = getLocalDateTime(formatTimeStr,pattern, zoneId);
        return getInstant(ldt,zoneId);
    }

    public static String format(long unixTime, FastDateFormatPattern pattern) {
        switch (pattern) {
            case ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT:
            case ISO_8601_EXTENDED_DATETIME_NO_T_TIME_ZONE_FORMAT:
                return DateFormatUtils.format(unixTime, pattern.getFormatPattern());
        }

        LocalDateTime localDateTime = getLocalDateTime(unixTime);
        return localDateTime.format(DateTimeFormatter.ofPattern(pattern.getFormatPattern()));
    }

    public static String format(String from_format_str, FastDateFormatPattern from_pattern, FastDateFormatPattern to_pattern) throws ParseException {
        if(from_pattern==null||to_pattern==null){
            throw new RuntimeException("from_pattern or to_pattern is null");
        }

        LocalDateTime ldt = getLocalDateTime(from_format_str, from_pattern);
        return format(ldt,to_pattern);
    }

    public static String format(LocalDateTime localDateTime, FastDateFormatPattern pattern) {
        switch (pattern) {
            case ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT:
            case ISO_8601_EXTENDED_DATETIME_NO_T_TIME_ZONE_FORMAT:
                return DateFormatUtils.format(getUnixTimeStamp(localDateTime), pattern.getFormatPattern());
        }

        return localDateTime.format(DateTimeFormatter.ofPattern(pattern.getFormatPattern()));
    }

    public static String format(LocalDate ld, TimeMode timeMode, FastDateFormatPattern pattern, ZoneId... zoneId) {
        LocalDateTime localDateTime = getLocalDateTime(ld,timeMode,zoneId);
        return format(localDateTime, pattern);
    }

    public static String format(Date date,FastDateFormatPattern pattern, ZoneId... zoneId) {
        LocalDateTime localDateTime = getLocalDateTime(date,zoneId);
        return format(localDateTime, pattern);
    }

    public static long plusYear(long unixTime, int years){
        Instant instant = getInstant(unixTime);
        plusYear(instant,years);
        // 转换回时间戳（毫秒）
        return instant.toEpochMilli();
    }

    public static long plusMonth(long unixTime, int months){
        Instant instant =getInstant(unixTime);
        plusMonth(instant,months);
        // 转换回时间戳（毫秒）
        return instant.toEpochMilli();
    }

    public static long plusDay(long unixTime, int days){
        return unixTime+days*24*3600*1000L;
    }

    public static long plusHour(long unixTime, int hours){
        return unixTime+hours*3600*1000L;
    }

    public static long plusMinute(long unixTime, int minutes){
        return unixTime + minutes*60*1000L;
    }

    public static long plusSecond(long unixTime, int seconds){
        return unixTime + seconds*1000L;
    }

    public static Instant plusYear(Instant instant, int years){
        instant.plus(years, ChronoUnit.YEARS);
        return instant;
    }

    public static Instant plusMonth(Instant instant, int months){
        instant.plus(months, ChronoUnit.MONTHS);
        return instant;
    }

    public static Instant plusDay(Instant instant, int days){
        instant.plus(days, ChronoUnit.DAYS);
        return instant;
    }

    public static Instant plusHour(Instant instant, int hours){
        instant.plus(hours, ChronoUnit.HOURS);
        return instant;
    }

    public static Instant plusMinute(Instant instant, int minutes){
        instant.plus(minutes, ChronoUnit.MINUTES);
        return instant;
    }

    public static Instant plusSecond(Instant instant, int seconds){
        instant.plus(seconds, ChronoUnit.SECONDS);
        return instant;
    }


    public static Calendar plusYear(Calendar calendar, int years){
        calendar.add(Calendar.YEAR, years);
        return calendar;
    }

    public static Calendar plusMonth(Calendar calendar, int months){
        calendar.add(Calendar.MONTH, months);
        return calendar;
    }

    public static Calendar plusDay(Calendar calendar, int days){
        calendar.add(Calendar.DATE, days);
        return calendar;
    }

    public static Calendar plusHour(Calendar calendar, int hours){
        calendar.add(Calendar.HOUR, hours);
        return calendar;
    }

    public static Calendar plusMinute(Calendar calendar, int minutes){
        calendar.add(Calendar.MINUTE, minutes);
        return calendar;
    }

    public static Calendar plusSecond(Calendar calendar, int seconds){
        calendar.add(Calendar.SECOND, seconds);
        return calendar;
    }

    public static LocalDateTime plusYear(LocalDateTime ldt, int years,ZoneId... zoneId){
        Instant instant=getInstant(ldt,zoneId);
        plusYear(instant,years);
        return getLocalDateTime(instant,zoneId);
    }

    public static LocalDateTime plusMonth(LocalDateTime ldt, int months,ZoneId... zoneId){
        Instant instant=getInstant(ldt,zoneId);
        plusMonth(instant,months);
        return getLocalDateTime(instant,zoneId);
    }

    public static LocalDateTime plusDay(LocalDateTime ldt, int days,ZoneId... zoneId){
        Instant instant=getInstant(ldt,zoneId);
        plusDay(instant,days);
        return getLocalDateTime(instant,zoneId);
    }

    public static LocalDateTime plusHour(LocalDateTime ldt, int hours,ZoneId... zoneId){
        Instant instant=getInstant(ldt,zoneId);
        plusHour(instant,hours);
        return getLocalDateTime(instant,zoneId);
    }

    public static LocalDateTime plusMinute(LocalDateTime ldt, int minutes,ZoneId... zoneId){
        Instant instant=getInstant(ldt,zoneId);
        plusMinute(instant,minutes);
        return getLocalDateTime(instant,zoneId);
    }

    public static LocalDateTime plusSecond(LocalDateTime ldt, int seconds,ZoneId... zoneId){
        Instant instant=getInstant(ldt,zoneId);
        plusSecond(instant,seconds);
        return getLocalDateTime(instant,zoneId);
    }

    public static LocalDate plusYear(LocalDate ld, int years){
        LocalDate new_ld=ld.plusYears(years);
        return new_ld;
    }

    public static LocalDate plusMonth(LocalDate ld, int months){
        LocalDate new_ld=ld.plusMonths(months);
        return new_ld;
    }

    public static LocalDate plusDay(LocalDate ld, int days){
        LocalDate new_ld=ld.plusDays(days);
        return new_ld;
    }


    /**
     * 判断指定的时间是否是在当天之中
     *
     * @param time
     * @return true: 是 ，false:否
     */
    public static boolean duringCurrDay(long time) {
        LocalDateTime todayZeroLDT = getLocalDateTime(TimeMode.ZERO_ZERO);
        LocalDateTime tomorrowZeroLDT = plusDay(todayZeroLDT, 1);
        Long startTime = getUnixTimeStamp(todayZeroLDT);
        Long endTime = getUnixTimeStamp(tomorrowZeroLDT);
        return during(time, startTime, endTime);
    }


    /**
     * 判断指定的时间是否在指定的两个时间之中之中
     *
     * @param time
     * @param startTime
     * @param endTime
     * @param isEndTimeClosure 结束时间是否闭合，如果true,则比较time<=endTime,否则比较time < endTime，默认为true
     * @return true: 是 ，false:否
     */
    public static boolean during(
            long time, long startTime, long endTime, boolean... isEndTimeClosure) {
        boolean endTimeClosure = true;
        if (ObjectUtils.isNotEmpty(isEndTimeClosure)) {
            endTimeClosure = isEndTimeClosure[0];
        }
        if (endTimeClosure) {
            if (time >= startTime && time <= endTime) {
                return true;
            }
        } else {
            if (time >= startTime && time < endTime) {
                return true;
            }
        }

        return false;
    }

    /**
     * 根据指定的年月日设置年月日
     */
    private static void setYYYYMMDD(Calendar calendar, int year, int month, int day) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DATE, day);
    }

    /**
     * 根据指定的时分秒设置时分秒
     */
    private static void setHHmmss(Calendar calendar, int hour, int minute, int second) {
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    /**
     * 根据指定的时间模式设置时分秒
     *
     * @param calendar
     * @param timeMode ZERO_ZERO-00:00:00
     *                 <p>TWENTY_THREE-23:59:59
     *                 <p>CURRENT-当前时间,如果为null，默认为当前的时间
     */
    private static void setHHmmss(Calendar calendar, TimeMode timeMode) {
        switch (timeMode) {
            case ZERO_ZERO:
                setHHmmss(calendar, 0, 0, 0);
                break;
            case TWENTY_THREE:
                setHHmmss(calendar, 23, 59, 59);
                break;
            default:
                break;
        }
    }


    /**
     * 获取指定的时区
     *
     * @param zoneIds 如果为空，则获取UTC+8时区
     * @return
     */
    public static ZoneId getZoneId(ZoneId... zoneIds) {
        if (zoneIds != null && zoneIds.length > 0) {
            return zoneIds[0];
        }
        return ZoneId.of("Asia/Shanghai");
    }

    public static TimeMode getTimeMode(TimeMode... timeMode) {
        if (timeMode != null && timeMode.length > 0) {
            return timeMode[0];
        }
        return TimeMode.CURRENT;
    }

    public static FastDateFormatPattern getFastDateFormatPattern(FastDateFormatPattern... patterns) {
        FastDateFormatPattern pattern;
        if (patterns == null || patterns.length == 0) {
            pattern = FastDateFormatPattern.ISO_DATETIME_NO_T_FORMAT;
        }
        else {
            pattern = patterns[0];
        }
        return pattern;
    }

}
