package com.jadyer.seed.comm.util;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 日期工具类
 * ----------------------------------------------------------------------------------------------------------------
 * @version v1.3
 * @version v1.3-->增加getDistanceDay()方法，用於計算兩個日期相隔的天數
 * @version v1.2-->增加getCrossDayList()方法，用于获取两个日期之间的所有日期列表
 * @history v1.1-->增加根据日期获得星期的方法getWeekName()
 * @history v1.0-->新建不添加若干方法
 * ----------------------------------------------------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.github.io/> on 2017/5/19 11:05.
 */
public final class DateUtil {
    private DateUtil(){}

    public static boolean isFirstDayOfMonth(){
        return "01".equals(DateFormatUtils.format(new Date(), "dd"));
    }


    public static boolean isFirstDayOfWeek(){
        return 0 == DateUtils.truncatedCompareTo(new Date(), getFirstDayOfWeek(), Calendar.DAY_OF_MONTH);
    }


    public static Date getFirstDayOfWeek(){
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return cal.getTime();
    }


    public static String getYestoday(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        return new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());
    }


    public static String getDetailDate(String dateStr){
        try {
            return String.format("%tF", DateUtils.parseDate(dateStr, "yyyyMMdd"));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getStringDate(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return df.format(new Date());
    }


    public static String getCurrentDate(){
        return new SimpleDateFormat("yyyyMMdd").format(new Date());
    }


    public static String getCurrentTime(){
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }


    public static Date getCurrentWeekStartDate(){
        Calendar currentDate = Calendar.getInstance();
        currentDate.setFirstDayOfWeek(Calendar.MONDAY);
        currentDate.set(Calendar.HOUR_OF_DAY, 0);
        currentDate.set(Calendar.MINUTE, 0);
        currentDate.set(Calendar.SECOND, 0);
        currentDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return currentDate.getTime();
    }


    public static Date getCurrentWeekEndDate(){
        Calendar currentDate = Calendar.getInstance();
        currentDate.setFirstDayOfWeek(Calendar.MONDAY);
        currentDate.set(Calendar.HOUR_OF_DAY, 23);
        currentDate.set(Calendar.MINUTE, 59);
        currentDate.set(Calendar.SECOND, 59);
        currentDate.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        return currentDate.getTime();
    }


    public static String getDistanceTime(Date begin, Date end) {
        long time = end.getTime() - begin.getTime();
        long day = time / (24 * 60 * 60 * 1000);
        long hour = (time / (60 * 60 * 1000) - day * 24);
        long minute = ((time / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long second = (time / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - minute * 60);
        return day + "天" + hour + "小时" + minute + "分" + second + "秒";
    }


    public static long getDistanceDay(Date begin, Date end) {
        String pattern = "yyyyMMdd";
        try {
            begin = DateUtils.parseDate(DateFormatUtils.format(begin, pattern), pattern);
            end = DateUtils.parseDate(DateFormatUtils.format(end, pattern), pattern);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long time = end.getTime() - begin.getTime();
        return time / (24 * 60 * 60 * 1000);
    }


    public static Date getIncreaseDate(Date startDate, int days){
        final Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.add(Calendar.DAY_OF_MONTH, days);
        return cal.getTime();
    }


    public static String getWeekName(String dateStr){
        dateStr = dateStr.replaceAll("-", "");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date date;
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            throw new IllegalArgumentException("无效入参，格式应为20170719或者2017-07-19");
        }
        return getWeekName(date);
    }


    public static String getWeekName(Date date){
        String[] weekNames = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int weekNameIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if(weekNameIndex < 0){
            weekNameIndex = 0;
        }
        return weekNames[weekNameIndex];
    }


    public static List<Integer> getCrossDayList(String startDate, String endDate){
        List<Integer> dayList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar startDay = Calendar.getInstance();
        Calendar endDay = Calendar.getInstance();
        try {
            startDay.setTime(sdf.parse(startDate));
            endDay.setTime(sdf.parse(endDate));
        } catch (ParseException e) {
            throw new IllegalArgumentException("无效入参：" + e.getMessage());
        }
        if(startDay.compareTo(endDay) >= 0){
            return dayList;
        }
        while(true){
            startDay.add(Calendar.DATE, 1);
            if(startDay.compareTo(endDay) == 0){
                break;
            }
            dayList.add(Integer.parseInt(DateFormatUtils.format(startDay.getTime(), "yyyyMMdd")));
        }
        return dayList;
    }

    public long GetTimems(String timestring) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        return  sdf.parse(timestring).getTime();
    }


}