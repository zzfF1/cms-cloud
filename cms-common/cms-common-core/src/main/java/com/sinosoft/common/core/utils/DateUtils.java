package com.sinosoft.common.core.utils;

import cn.hutool.core.date.DateUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.time.DateFormatUtils;
import com.sinosoft.common.core.enums.FormatsType;
import com.sinosoft.common.core.exception.ServiceException;

import java.lang.management.ManagementFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

/**
 * 时间工具类
 *
 * @author cms
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {


    private static final String[] PARSE_PATTERNS = {
        "yyyyMMdd",
        "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
        "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
        "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

    /**
     * Excel 数字日期格式
     */
    private static final Calendar EXCEL_CALENDER;

    static {
        EXCEL_CALENDER = new GregorianCalendar(1900, 0, -1);
    }

    /**
     * 获取当前日期和时间
     *
     * @return 当前日期和时间的 Date 对象表示
     */
    public static Date getNowDate() {
        return new Date();
    }

    /**
     * 获取当前日期的字符串表示，格式为YYYY-MM-DD
     *
     * @return 当前日期的字符串表示
     */
    public static String getDate() {
        return dateTimeNow(FormatsType.YYYY_MM_DD);
    }

    /**
     * 获取当前日期的字符串表示，格式为yyyyMMdd
     *
     * @return 当前日期的字符串表示
     */
    public static String getCurrentDate() {
        return DateFormatUtils.format(new Date(), FormatsType.YYYYMMDD.getTimeFormat());
    }

    /**
     * 获取当前日期的路径格式字符串，格式为"yyyy/MM/dd"
     *
     * @return 当前日期的路径格式字符串
     */
    public static String datePath() {
        Date now = new Date();
        return DateFormatUtils.format(now, FormatsType.YYYY_MM_DD_SLASH.getTimeFormat());
    }

    /**
     * 获取当前时间的字符串表示，格式为YYYY-MM-DD HH:MM:SS
     *
     * @return 当前时间的字符串表示
     */
    public static String getTime() {
        return dateTimeNow(FormatsType.YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 获取当前时间的字符串表示，格式为 "HH:MM:SS"
     *
     * @return 当前时间的字符串表示，格式为 "HH:MM:SS"
     */
    public static String getTimeWithHourMinuteSecond() {
        return dateTimeNow(FormatsType.HH_MM_SS);
    }

    /**
     * 获取当前日期和时间的字符串表示，格式为YYYYMMDDHHMMSS
     *
     * @return 当前日期和时间的字符串表示
     */
    public static String dateTimeNow() {
        return dateTimeNow(FormatsType.YYYYMMDDHHMMSS);
    }

    /**
     * 获取当前日期和时间的指定格式的字符串表示
     *
     * @param format 日期时间格式，例如"YYYY-MM-DD HH:MM:SS"
     * @return 当前日期和时间的字符串表示
     */
    public static String dateTimeNow(final FormatsType format) {
        return parseDateToStr(format, new Date());
    }

    /**
     * 将指定日期格式化为 YYYY-MM-DD 格式的字符串
     *
     * @param date 要格式化的日期对象
     * @return 格式化后的日期字符串
     */
    public static String formatDate(final Date date) {
        return parseDateToStr(FormatsType.YYYY_MM_DD, date);
    }

    /**
     * 将指定日期格式化为 YYYY-MM-DD HH:MM:SS 格式的字符串
     *
     * @param date 要格式化的日期对象
     * @return 格式化后的日期时间字符串
     */
    public static String formatDateTime(final Date date) {
        return parseDateToStr(FormatsType.YYYY_MM_DD_HH_MM_SS, date);
    }

    /**
     * 将指定日期按照指定格式进行格式化
     *
     * @param format 要使用的日期时间格式，例如"YYYY-MM-DD HH:MM:SS"
     * @param date   要格式化的日期对象
     * @return 格式化后的日期时间字符串
     */
    public static String parseDateToStr(final FormatsType format, final Date date) {
        return new SimpleDateFormat(format.getTimeFormat()).format(date);
    }

    /**
     * 将指定格式的日期时间字符串转换为 Date 对象
     *
     * @param format 要解析的日期时间格式，例如"YYYY-MM-DD HH:MM:SS"
     * @param ts     要解析的日期时间字符串
     * @return 解析后的 Date 对象
     * @throws RuntimeException 如果解析过程中发生异常
     */
    public static Date parseDateTime(final FormatsType format, final String ts) {
        try {
            return new SimpleDateFormat(format.getTimeFormat()).parse(ts);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将对象转换为日期对象
     *
     * @param str 要转换的对象，通常是字符串
     * @return 转换后的日期对象，如果转换失败或输入为null，则返回null
     */
    public static Date parseDate(Object str) {
        if (str == null) {
            return null;
        }
        try {
            return parseDate(str.toString(), PARSE_PATTERNS);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 获取服务器启动时间
     *
     * @return 服务器启动时间的 Date 对象表示
     */
    public static Date getServerStartDate() {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        return new Date(time);
    }

    /**
     * 计算两个日期之间的天数差（以毫秒为单位）
     *
     * @param date1 第一个日期
     * @param date2 第二个日期
     * @return 两个日期之间的天数差的绝对值
     */
    public static int differentDaysByMillisecond(Date date1, Date date2) {
        return Math.abs((int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24)));
    }

    /**
     * 计算两个日期之间的时间差，并以天、小时和分钟的格式返回
     *
     * @param endDate 结束日期
     * @param nowDate 当前日期
     * @return 表示时间差的字符串，格式为"天 小时 分钟"
     */
    public static String getDatePoor(Date endDate, Date nowDate) {
        long diffInMillis = endDate.getTime() - nowDate.getTime();
        long day = TimeUnit.MILLISECONDS.toDays(diffInMillis);
        long hour = TimeUnit.MILLISECONDS.toHours(diffInMillis) % 24;
        long min = TimeUnit.MILLISECONDS.toMinutes(diffInMillis) % 60;
        return String.format("%d天 %d小时 %d分钟", day, hour, min);
    }

    /**
     * 计算两个时间点的差值（天、小时、分钟、秒），当值为0时不显示该单位
     *
     * @param endDate 结束时间
     * @param nowDate 当前时间
     * @return 时间差字符串，格式为 "x天 x小时 x分钟 x秒"，若为 0 则不显示
     */
    public static String getTimeDifference(Date endDate, Date nowDate) {
        long diffInMillis = endDate.getTime() - nowDate.getTime();
        long day = TimeUnit.MILLISECONDS.toDays(diffInMillis);
        long hour = TimeUnit.MILLISECONDS.toHours(diffInMillis) % 24;
        long min = TimeUnit.MILLISECONDS.toMinutes(diffInMillis) % 60;
        long sec = TimeUnit.MILLISECONDS.toSeconds(diffInMillis) % 60;
        // 构建时间差字符串，条件是值不为0才显示
        StringBuilder result = new StringBuilder();
        if (day > 0) {
            result.append(String.format("%d天 ", day));
        }
        if (hour > 0) {
            result.append(String.format("%d小时 ", hour));
        }
        if (min > 0) {
            result.append(String.format("%d分钟 ", min));
        }
        if (sec > 0) {
            result.append(String.format("%d秒", sec));
        }
        return result.length() > 0 ? result.toString().trim() : "0秒";
    }

    /**
     * 将 LocalDateTime 对象转换为 Date 对象
     *
     * @param temporalAccessor 要转换的 LocalDateTime 对象
     * @return 转换后的 Date 对象
     */
    public static Date toDate(LocalDateTime temporalAccessor) {
        ZonedDateTime zdt = temporalAccessor.atZone(ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }

    /**
     * 将 LocalDate 对象转换为 Date 对象
     *
     * @param temporalAccessor 要转换的 LocalDate 对象
     * @return 转换后的 Date 对象
     */
    public static Date toDate(LocalDate temporalAccessor) {
        LocalDateTime localDateTime = LocalDateTime.of(temporalAccessor, LocalTime.of(0, 0, 0));
        ZonedDateTime zdt = localDateTime.atZone(ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }

    /**
     * 校验日期范围
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param maxValue  最大时间跨度的限制值
     * @param unit      时间跨度的单位，可选择 "DAYS"、"HOURS" 或 "MINUTES"
     */
    public static void validateDateRange(Date startDate, Date endDate, int maxValue, TimeUnit unit) {
        // 校验结束日期不能早于开始日期
        if (endDate.before(startDate)) {
            throw new ServiceException("结束日期不能早于开始日期");
        }

        // 计算时间跨度
        long diffInMillis = endDate.getTime() - startDate.getTime();

        // 根据单位转换时间跨度
        long diff = switch (unit) {
            case DAYS -> TimeUnit.MILLISECONDS.toDays(diffInMillis);
            case HOURS -> TimeUnit.MILLISECONDS.toHours(diffInMillis);
            case MINUTES -> TimeUnit.MILLISECONDS.toMinutes(diffInMillis);
            default -> throw new IllegalArgumentException("不支持的时间单位");
        };

        // 校验时间跨度不超过最大限制
        if (diff > maxValue) {
            throw new ServiceException("最大时间跨度为 " + maxValue + " " + unit.toString().toLowerCase());
        }
    }

    /**
     * excel 数字日期转换为字符串
     *
     * @param num 日期天数
     * @return 日期字符串
     */
    public static String parseNumToDateStr(final String num) {
        return DateUtils.formatDate(DateUtils.addDays(EXCEL_CALENDER.getTime(), Integer.parseInt(num)));
    }

    /**
     * 获取对应日期所属的年月
     *
     * @param date 日期
     * @return 年月
     */
    public static String getCurYearMonth(Date date) {
        return DateUtil.format(date, FormatsType.YYYYMM.getTimeFormat());
    }

    /**
     * 根据分割日计算对应日期所属的佣金年月
     *
     * @param date      日期
     * @param separator 分割日
     * @return 佣金年月
     */
    public static String getYearMonth(Date date, int separator) {
        int day = DateUtil.dayOfMonth(date);
        if (day >= separator) {
            return parseDateToStr(FormatsType.YYYYMM, date);
        } else {
            //获取当前日期的次月
            Date nextMonthDate = DateUtil.offsetMonth(date, 1);
            return parseDateToStr(FormatsType.YYYYMM, nextMonthDate);
        }
    }
}
