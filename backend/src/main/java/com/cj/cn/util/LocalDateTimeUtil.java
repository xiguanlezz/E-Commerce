package com.cj.cn.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeUtil {
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * LocalDateTime对象转String
     *
     * @param localDateTime
     * @param dateFormat
     * @return
     */
    public static String dateToStr(LocalDateTime localDateTime, String dateFormat) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
        return localDateTime.format(formatter);
    }

    public static String dateToStr(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        return localDateTime.format(formatter);
    }

    /**
     * String转LocalDateTime对象
     *
     * @param dateTime
     * @param dateFormat
     * @return
     */
    public static LocalDateTime strToDate(String dateTime, String dateFormat) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
        return LocalDateTime.parse(dateTime, formatter);
    }

    public static LocalDateTime strToDate(String dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        return LocalDateTime.parse(dateTime, formatter);
    }

    public static void main(String[] args) {
        System.out.println(LocalDateTimeUtil.dateToStr(LocalDateTime.now()));
        System.out.println(LocalDateTimeUtil.strToDate("2020-09-10 18:38:20"));
    }
}
