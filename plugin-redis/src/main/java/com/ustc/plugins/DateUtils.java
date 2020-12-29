package com.ustc.plugins;

import java.util.Calendar;
import java.util.Date;

/**
 * company: guochuang software co.ltd<br>
 * date: 2020/4/8<br>
 * filename: DateUtils<br>
 * <p>
 * description:<br>
 * 日期工具类
 * </p>
 *
 * @author xie.bowen@ustcinfo.com
 */
public class DateUtils {

    /**
     * 明天0点
     *
     * @return 日期
     */
    public static Date nextDay() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);

        return calendar.getTime();
    }

    /**
     * 获取指定时间
     *
     * @param hour   小时
     * @param minute 分钟
     * @param second 秒
     * @return 日期
     */
    public static Date nextDay(int hour, int minute, int second) {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.HOUR_OF_DAY, hour);

        return calendar.getTime();
    }


    /**
     * 设置一周中的时间点
     *
     * @param dayOfWeek 一周中的日期
     * @param hour      小时
     * @param minute    分钟
     * @param second    秒
     * @return 日期
     */
    public static Date nextWeek(int dayOfWeek, int hour, int minute, int second) {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        return calendar.getTime();
    }

    /**
     * 下周一0点
     *
     * @return 日期
     */
    public static Date nextWeek() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.DAY_OF_WEEK, 2);
        return calendar.getTime();
    }

    /**
     * 设置一月中的时间点
     *
     * @param dayOfMonth 一月中的日期
     * @param hour       小时
     * @param minute     分钟
     * @param second     秒
     * @return 日期
     */
    public static Date nextMonth(int dayOfMonth, int hour, int minute, int second) {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.DAY_OF_WEEK, dayOfMonth);
        return calendar.getTime();
    }

    /**
     * 下月0点
     *
     * @return 日期
     */
    public static Date nextMonth() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }


    public static void main(String[] args) {
        System.out.println(nextDay());
        System.out.println(nextWeek());
        System.out.println(nextMonth());
    }
}
