package org.chen.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * 获取时间字符串
 *
 * @date 2019/2/13 14:20
 **/
 
public class TimeUtil {

    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public static Map<String,Object> getToday(Map<String,Object> param){
        Calendar c = Calendar.getInstance();
        return getDay(param,c);
    }

    public static Map<String,Object> getYestaday(Map<String,Object> param){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH,-1);
        return getDay(param,c);
    }

    public static Map<String,Object> getThisMonth(Map<String,Object> param){
        Calendar c = Calendar.getInstance();
        return getMonthDay(param,c);
    }

    public static Map<String,Object> getLastMonth(Map<String,Object> param){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH,-1);
        return getMonthDay(param,c);
    }

    private static Map<String,Object> getMonthDay(Map<String,Object> param,Calendar c){
        // 第一天
        c.set(Calendar.DAY_OF_MONTH,1);
        String monthStart = sdf.format(c.getTime());
        // 最后一天
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        String monthEnd = sdf.format(c.getTime());
        return getMap(param,monthStart,monthEnd);
    }

    private static Map<String,Object> getDay(Map<String,Object> param,Calendar c){
        String day = sdf.format(c.getTime());
        return getMap(param,day,day);
    }

    private static Map<String,Object> getMap(Map<String,Object> param ,String start,String end){
        end += " 23:59:59";
        start += " 00:00:00";
        param.put("start",start);
        param.put("end",end);
        return param;
    }


    public static Calendar getThisMonthFirst(){
        // 本月第一天
        Calendar thisMonthFirst = Calendar.getInstance();
        thisMonthFirst.setTime(new Date());
        thisMonthFirst.set(Calendar.DAY_OF_MONTH,1);
        thisMonthFirst.set(Calendar.HOUR_OF_DAY, 0);
        thisMonthFirst.set(Calendar.MINUTE, 0);
        thisMonthFirst.set(Calendar.SECOND, 0);
        thisMonthFirst.set(Calendar.MILLISECOND, 0);
        return thisMonthFirst;
    }

    public static Calendar getThisMonthLast(){
        // 本月最后一天
        Calendar thisMonthLast = Calendar.getInstance();
        thisMonthLast.setTime(new Date());
        thisMonthLast.set(Calendar.DAY_OF_MONTH, thisMonthLast.getActualMaximum(Calendar.DAY_OF_MONTH));
        thisMonthLast.set(Calendar.HOUR_OF_DAY, 23);
        thisMonthLast.set(Calendar.MINUTE, 59);
        thisMonthLast.set(Calendar.SECOND, 59);
        thisMonthLast.set(Calendar.MILLISECOND, 999);
        return thisMonthLast;
    }

    public static Calendar getLastMonthFirst(){
        // 上月第一天
        Calendar thisMonthFirst = Calendar.getInstance();
        thisMonthFirst.setTime(new Date());
        thisMonthFirst.add(Calendar.MONTH,-1);
        thisMonthFirst.set(Calendar.DAY_OF_MONTH,1);
        thisMonthFirst.set(Calendar.HOUR_OF_DAY, 0);
        thisMonthFirst.set(Calendar.MINUTE, 0);
        thisMonthFirst.set(Calendar.SECOND, 0);
        thisMonthFirst.set(Calendar.MILLISECOND, 0);
        return thisMonthFirst;
    }

    public static Calendar getLastMonthLast(){
        // 上月最后一天
        Calendar thisMonthLast = Calendar.getInstance();
        thisMonthLast.setTime(new Date());
        thisMonthLast.add(Calendar.MONTH,-1);
        thisMonthLast.set(Calendar.DAY_OF_MONTH, thisMonthLast.getActualMaximum(Calendar.DAY_OF_MONTH));
        thisMonthLast.set(Calendar.HOUR_OF_DAY, 23);
        thisMonthLast.set(Calendar.MINUTE, 59);
        thisMonthLast.set(Calendar.SECOND, 59);
        thisMonthLast.set(Calendar.MILLISECOND, 999);
        return thisMonthLast;
    }

    public static Calendar getTodayStart(){
        // 今天的开始
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart;
    }

    public static Calendar getTodayEnd(){
        // 今天的结束
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        return todayEnd;
    }

    public static Calendar getYesterdayStart(){
        // 昨天的开始
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_MONTH,-1);
        yesterday.set(Calendar.HOUR_OF_DAY, 0);
        yesterday.set(Calendar.MINUTE, 0);
        yesterday.set(Calendar.SECOND, 0);
        yesterday.set(Calendar.MILLISECOND, 0);
        return yesterday;
    }

    public static Calendar getYesterdayEnd(){
        // 昨天的结束
        Calendar yesterdayEnd = Calendar.getInstance();
        yesterdayEnd.add(Calendar.DAY_OF_MONTH,-1);
        yesterdayEnd.set(Calendar.HOUR_OF_DAY, 23);
        yesterdayEnd.set(Calendar.MINUTE, 59);
        yesterdayEnd.set(Calendar.SECOND, 59);
        yesterdayEnd.set(Calendar.MILLISECOND, 999);
        return yesterdayEnd;
    }

}
