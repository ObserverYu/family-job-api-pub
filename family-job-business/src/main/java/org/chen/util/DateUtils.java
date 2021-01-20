package org.chen.util;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间工具类
 * 
 * @author huanglei
 */
public class DateUtils {
	/** 年 */
	public static final String FORMAT_yyyy = "yyyy";
	/** 年月 */
	public static final String FORMAT_yyyy_MM = "yyyy-MM";
	/** 年月日 */
	public static final String FORMAT_yyyy_MM_dd = "yyyy-MM-dd";
	/** 年月日时 */
	public static final String FORMAT_yyyy_MM_dd_HH = "yyyy-MM-dd HH";
	/** 年月日时分秒 */
	public static final String FORMAT_yyyy_MM_dd_HH_mm = "yyyy-MM-dd HH:mm";
	/** 年月日时分秒 */
	public static final String FORMAT_yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";
	/** 时分秒，起始时间 */
	public static final String HH_mm_ss_START_TIME = " 00:00:00";
	/** 时分秒，结束时间 */
	public static final String HH_mm_ss_END_TIME = "  23:59:59";
	/** 分秒，结束时间 */
	public static final String mm_ss_END_TIME = ":59:59";
	/** 分，结束时间 */
	public static final String mm_END_TIME = ":59";
	/**
	 * 获取 当前年、半年、季度、月、日、小时 开始结束时间
	 */
	private static final SimpleDateFormat yearSdf;
	private static final SimpleDateFormat shortSdf;
	private static final SimpleDateFormat longHourSdf;
	private static final SimpleDateFormat longMinuteSdf;
	private static final SimpleDateFormat longSdf;

	static {
		yearSdf = new SimpleDateFormat(FORMAT_yyyy);
		shortSdf = new SimpleDateFormat(FORMAT_yyyy_MM_dd);
		longHourSdf = new SimpleDateFormat(FORMAT_yyyy_MM_dd_HH);
		longMinuteSdf = new SimpleDateFormat(FORMAT_yyyy_MM_dd_HH_mm);
		longSdf = new SimpleDateFormat(FORMAT_yyyy_MM_dd_HH_mm_ss);
	}

	/**
	 * 昨天的日期 格式yyyy-MM-dd
	 *
	 * @return
	 */
	public static String getYesterdayDateStr() {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_yyyy_MM_dd);
		calendar.add(Calendar.DATE,-1);
		return sdf.format(calendar.getTime());
	}

	/**
	 * 昨天的日期
	 *
	 * @return
	 */
	public static String getYesterdayDateStr(String format) {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		calendar.add(Calendar.DATE,-1);
		return sdf.format(calendar.getTime());
	}


	public static Date parse(String time, String pattern) {
		if(!StringUtils.isBlank(time) && !StringUtils.isBlank(pattern)) {
			SimpleDateFormat format = new SimpleDateFormat(pattern);

			try {
				return format.parse(time);
			} catch (ParseException var4) {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * 是否同一天
	 */
	public static boolean isSameDay(Date date1, Date date2) {
		if (shortSdf.format(date1).equals(shortSdf.format(date2))) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 是否同一年
	 */
	public static boolean isSameYear(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR) ? true : false;
	}

	/**
	 * 是否闰年
	 * 
	 * @param year
	 * @return
	 */
	public static boolean isLeapYear(int year) {
		return (year % 400 == 0) || (year % 4 == 0) && (year % 100 != 0);
	}

	/**
	 * 是否闰年
	 *
	 * @return
	 */
	public static boolean isLeapYear(Date date) {
		int year = getYear(date);
		return (year % 400 == 0) || (year % 4 == 0) && (year % 100 != 0);
	}

	/**
	 * 某年某月的天数
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public static int calculateTheDay(int year, int month) {
		boolean leapYear = isLeapYear(year);
		int day;
		if (leapYear && month == 2) {
			day = 29;
		} else if (!leapYear && month == 2) {
			day = 28;
		} else if (month == 4 || month == 6 || month == 9 || month == 11) {
			day = 30;
		} else {
			day = 31;
		}
		return day;
	}

	/**
	 * 某年某月的天数
	 *
	 * @return
	 */
	public static int calculateTheDay(Date date) {
		int month = getMonth(date);
		boolean leapYear = isLeapYear(date);
		int day;
		if (leapYear && month == 2) {
			day = 29;
		} else if (!leapYear && month == 2) {
			day = 28;
		} else if (month == 4 || month == 6 || month == 9 || month == 11) {
			day = 30;
		} else {
			day = 31;
		}
		return day;
	}

	/**
	 * 是否月份最后一天
	 * 
	 * @param date
	 * @return
	 */
	public static boolean isMonthEndDay(Date date) {
		int currentDay = getDay(date);// 当前日期
		int days = calculateTheDay(date);// 月份天数
		if (currentDay != days) {
			return false;
		}
		return true;
	}

	/**
	 * 获取下一个日期
	 * 
	 * @param format
	 * @param StrDate
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	@SuppressWarnings("static-access")
	public static String getNextDate(String format, String StrDate, int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		calendar.setTime(sdf.parse((StrDate), new ParsePosition(0)));
		if (day != 0) {
			calendar.add(calendar.DATE, day);
		}
		if (month != 0) {
			calendar.add(calendar.MONTH, month);
		}
		if (year != 0) {
			calendar.add(calendar.YEAR, year);
		}
		return sdf.format(calendar.getTime());
	}


	/**
	 * 获取下一个日期
	 * 
	 * @param format
	 * @param StrDate
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	@SuppressWarnings("static-access")
	public static String getNextDate(String format, Date date, int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		calendar.setTime(date);
		if (day != 0) {
			calendar.add(calendar.DATE, day);
		}
		if (month != 0) {
			calendar.add(calendar.MONTH, month);
		}
		if (year != 0) {
			calendar.add(calendar.YEAR, year);
		}
		return sdf.format(calendar.getTime());
	}

	/**
	 * 日期类型转字符串类型日期
	 * 
	 * @param s
	 * @return
	 */
	public static String dateToString(Date date) {
		try {
			return shortSdf.format(date);
		} catch (Exception e) {
			return null;
		}

	}

	/**
	 * 字符串类型日期转日期类型
	 * 
	 * @param s
	 * @return
	 */
	public static Date stringToDate(String s) {
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(shortSdf.parse(s));
		} catch (Exception e) {
			return null;
		}
		return calendar.getTime();
	}

	/**
	 * 根据月份，设置日期
	 * 
	 * @param month
	 * @return
	 */
	public static Date setDate(int month) {
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.set(getCurrentYear(), month, getCurrentDay());
		} catch (Exception e) {
			return null;
		}
		return calendar.getTime();
	}

	/**
	 * 获得当前年
	 * 
	 * @return
	 */
	public static int getCurrentYear() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		int year = calendar.get(Calendar.YEAR);
		return year;
	}

	/**
	 * 获得某个日期所属年份
	 * 
	 * @param date
	 * @return
	 */
	public static int getYear(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int year = calendar.get(Calendar.YEAR);
		return year;
	}

	/**
	 * 获得当前月
	 * 
	 * @return
	 */
	public static int getCurrentMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		int month = calendar.get(Calendar.MONTH) + 1;
		return month;
	}

	/**
	 * 获得某个时间所属月
	 * 
	 * @param date
	 * @return
	 */
	public static int getMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH) + 1;
		return month;
	}

	/**
	 * 获得当前天
	 * 
	 * @return
	 */
	public static int getCurrentDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		int day = calendar.get(Calendar.DATE);
		return day;
	}

	/**
	 * 获得某个日期所属天
	 * 
	 * @param date
	 * @return
	 */
	public static int getDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int day = calendar.get(Calendar.DATE);
		return day;
	}

	/**
	 * 转换为指定格式，返回字符串对象
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String format(Date date, String pattern) {
		try {
			if (date == null) {
				return null;
			}
			return DateFormatUtils.format(date, pattern);
		} catch (Exception e) {
			return StringUtils.EMPTY;
		}
	}

	/**
	 * 转换为指定格式，返回日期对象
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static Date formatDate(Date date, String pattern) {
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(shortSdf.parse(format(date, pattern)));
		} catch (Exception e) {
			return null;
		}
		return calendar.getTime();
	}

	/**
	 * long类型转Date类型
	 * 
	 * @param value
	 * @return
	 */
	public static Date longToDate(long value) {
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTimeInMillis(value);
		} catch (Exception e) {
			return null;
		}
		return calendar.getTime();
	}

	/**
	 * 增加年
	 * 
	 * @param s
	 * @param n
	 * @return
	 */
	public static Date addYear(String s, int n) {
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(shortSdf.parse(s));
			calendar.add(Calendar.YEAR, n);// 增加年
		} catch (Exception e) {
			return null;
		}
		return calendar.getTime();
	}

	/**
	 * 增加年
	 * 
	 * @param s
	 * @param n
	 * @return
	 */
	public static Date addYear(Date date, int n) {
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(date);
			calendar.add(Calendar.YEAR, n);// 增加年
		} catch (Exception e) {
			return null;
		}
		return calendar.getTime();
	}

	/**
	 * 增加天
	 * 
	 * @param s
	 * @param n
	 * @return
	 */
	public static Date addDay(String s, int n) {
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(shortSdf.parse(s));
			calendar.add(Calendar.DATE, n);// 增加天
		} catch (Exception e) {
			return null;
		}
		return calendar.getTime();
	}

	/**
	 * 增加天
	 * 
	 * @param s
	 * @param n
	 * @return
	 */
	public static Date addDay(Date date, int n) {
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(date);
			calendar.add(Calendar.DATE, n);// 增加天
		} catch (Exception e) {
			return null;
		}
		return calendar.getTime();
	}

	/**
	 * 增加月
	 * 
	 * @param date
	 * @param n
	 * @return
	 */
	public static Date addMonth(String s, int n) {
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(shortSdf.parse(s));
			calendar.add(Calendar.MONTH, n);// 增加月
		} catch (Exception e) {
			return null;
		}
		return calendar.getTime();
	}

	/**
	 * 增加月
	 * 
	 * @param date
	 * @param n
	 * @return
	 */
	public static Date addMonth(Date date, int n) {
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(date);
			calendar.add(Calendar.MONTH, n);// 增加月
		} catch (Exception e) {
			return null;
		}
		return calendar.getTime();
	}

	/**
	 * 增加小时
	 * 
	 * @param date
	 * @param n
	 * @return
	 */
	public static Date addHour(Date date, int n) {
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(date);
			calendar.add(Calendar.HOUR, n);// 增加小时
		} catch (Exception e) {
			return null;
		}
		return calendar.getTime();
	}

	/**
	 * 增加分钟
	 * 
	 * @param date
	 * @param n
	 * @return
	 */
	public static Date addMinute(Date date, int n) {
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(date);
			calendar.add(Calendar.MINUTE, n);// 增加分钟
		} catch (Exception e) {
			return null;
		}
		return calendar.getTime();
	}

	/**
	 * 获得本周的第一天，周一，如：2015-05-25
	 * 
	 * @return
	 */
	public static Date getCurrentWeekDayStartTime() {
		Calendar c = Calendar.getInstance();
		try {
			int weekday = c.get(Calendar.DAY_OF_WEEK) - 2;
			c.add(Calendar.DATE, -weekday);
			c.setTime(longSdf.parse(shortSdf.format(c.getTime()) + HH_mm_ss_START_TIME));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return c.getTime();
	}

	/**
	 * 获得下周的第一天，周一，如：2015-05-25
	 *
	 * @return
	 */
	public static Date getNextWeekDayStartTime() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.WEEK_OF_YEAR,1);
		try {
			int weekday = c.get(Calendar.DAY_OF_WEEK) - 2;
			c.add(Calendar.DATE, -weekday);
			c.setTime(longSdf.parse(shortSdf.format(c.getTime()) + HH_mm_ss_START_TIME));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return c.getTime();
	}

	/**
	 * 获得下周的第一天，周一，如：2015-05-25
	 *
	 * @return
	 */
	public static Calendar getNextWeekDayStartCalendar() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.WEEK_OF_YEAR,1);
		try {
			int weekday = c.get(Calendar.DAY_OF_WEEK) - 2;
			c.add(Calendar.DATE, -weekday);
			c.setTime(longSdf.parse(shortSdf.format(c.getTime()) + HH_mm_ss_START_TIME));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return c;
	}

	/**
	 * 获得本周的最后一天，周日，如：2015-05-31
	 * 
	 * @return
	 */
	public static Date getCurrentWeekDayEndTime() {
		Calendar c = Calendar.getInstance();
		try {
			int weekday = c.get(Calendar.DAY_OF_WEEK);
			c.add(Calendar.DATE, 8 - weekday);
			c.setTime(longSdf.parse(shortSdf.format(c.getTime()) + HH_mm_ss_END_TIME));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return c.getTime();
	}

	/**
	 * 获得本天的开始时间，如：2015-05-25 00:00:00
	 * 
	 * @return
	 */
	public static Date getCurrentDayStartTime() {
		Date now = new Date();
		try {
			now = shortSdf.parse(shortSdf.format(now));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return now;
	}

	/**
	 * 获得明天的开始时间，如：2015-05-25 00:00:00
	 *
	 * @return
	 */
	public static Date getNextDayStartTime() {
		Calendar next = Calendar.getInstance();
		next.add(Calendar.DATE,1);
		Date nextDay = next.getTime();
		try {
			nextDay = shortSdf.parse(shortSdf.format(nextDay));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nextDay;
	}

	/**
	 * 获得本天的结束时间，如：2015-05-25 23:59:59
	 * 
	 * @return
	 */
	public static Date getCurrentDayEndTime(Date date) {
		Date now = new Date();
		if (null != date) {
			now = date;
		}
		try {
			now = longSdf.parse(shortSdf.format(now) + HH_mm_ss_END_TIME);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return now;
	}

	/**
	 * 获得本小时的开始时间，如：2015-05-25 10:00:00
	 * 
	 * @return
	 */
	public static Date getCurrentHourStartTime() {
		Date now = new Date();
		try {
			now = longHourSdf.parse(longHourSdf.format(now));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return now;
	}

	/**
	 * 获得本分钟的开始时间，如：2015-05-25 10:00
	 * 
	 * @return
	 */
	public static Date getCurrentMinuteStartTime() {
		Date now = new Date();
		try {
			now = longMinuteSdf.parse(longMinuteSdf.format(now));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return now;
	}

	/**
	 * 获得本分钟的结束时间，如：2015-05-25 10:59
	 * 
	 * @return
	 */
	public static Date getCurrentMinuteEndTime(Date date) {
		Date now = new Date();
		if (null != date) {
			now = date;
		}
		try {
			String string = longMinuteSdf.format(now);
			string += mm_END_TIME;
			now = longSdf.parse(string);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return now;
	}

	/**
	 * 获得本月的开始时间，如：2015-05-01 00:00:00
	 * 
	 * @return
	 */
	public static Date getCurrentMonthStartTime() {
		Calendar c = Calendar.getInstance();
		Date now = null;
		try {
			c.set(Calendar.DATE, 1);
			now = shortSdf.parse(shortSdf.format(c.getTime()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return now;
	}

	/**
	 * 获得下月的开始时间，如：2015-05-01 00:00:00
	 *
	 * @return
	 */
	public static Date getNextMonthStartTime() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH,1);
		Date now = null;
		try {
			c.set(Calendar.DATE, 1);
			now = shortSdf.parse(shortSdf.format(c.getTime()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return now;
	}

	/**
	 * 获得下月的开始时间，如：2015-05-01 00:00:00
	 *
	 * @return
	 */
	public static Calendar getNextMonthStartCalendar() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH,1);
		try {
			c.set(Calendar.DATE, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return c;
	}

	/**
	 * 获取过去一个月的当天的时间，
	 * 
	 * @param date
	 * @return
	 */
	public static Date getLastMonthDate(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MONTH, -1);
		Date m = c.getTime();
		System.out.println("过去一个月：" + m);
		return m;
	}
	
	
	
	/**
	 * 获取某一个月的开始时间，如：2015-05-01 00:00:00
	 * 
	 * @param date
	 * @return
	 */
	public static Date getMonthStartTime(Date date) {
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(date);
			calendar.set(Calendar.DATE, 1);
			date = shortSdf.parse(shortSdf.format(calendar.getTime()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}


	/**
	 * 当前月的结束时间，如：2015-05-31 23:59:59
	 * 
	 * @return
	 */
	public static Date getCurrentMonthEndTime() {
		Calendar c = Calendar.getInstance();
		Date now = null;
		try {
			c.set(Calendar.DATE, 1);
			c.add(Calendar.MONTH, 1);
			c.add(Calendar.DATE, -1);
			now = longSdf.parse(shortSdf.format(c.getTime()) + HH_mm_ss_END_TIME);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return now;
	}

	/**
	 * 获取某个月的结束时间，如：2015-05-31 23:59:59
	 * 
	 * @param date
	 * @return
	 */
	public static Date getMonthEndTime(Date date) {
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(date);
			calendar.set(Calendar.DATE, 1);
			calendar.add(Calendar.MONTH, 1);
			calendar.add(Calendar.DATE, -1);
			date = longSdf.parse(shortSdf.format(calendar.getTime()) + HH_mm_ss_END_TIME);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 当前年的开始时间，如：2015-01-01 00:00:00
	 * 
	 * @return
	 */
	public static Date getCurrentYearStartTime() {
		Calendar c = Calendar.getInstance();
		Date now = null;
		try {
			c.set(Calendar.MONTH, 0);
			c.set(Calendar.DATE, 1);
			now = shortSdf.parse(shortSdf.format(c.getTime()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return now;
	}

	/**
	 * 当前年的结束时间，如：2015-12-31 23:59:59
	 * 
	 * @return
	 */
	public static Date getCurrentYearEndTime() {
		Calendar c = Calendar.getInstance();
		Date now = null;
		try {
			c.set(Calendar.MONTH, 11);
			c.set(Calendar.DATE, 31);
			now = longSdf.parse(shortSdf.format(c.getTime()) + HH_mm_ss_END_TIME);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return now;
	}

	/**
	 * 当前季度的开始时间，如：2015-04-01 00:00:00
	 * 
	 * @return
	 */
	public static Date getCurrentQuarterStartTime() {
		Calendar c = Calendar.getInstance();
		int currentMonth = c.get(Calendar.MONTH) + 1;
		Date now = null;
		try {
			if (currentMonth >= 1 && currentMonth <= 3)
				c.set(Calendar.MONTH, 0);
			else if (currentMonth >= 4 && currentMonth <= 6)
				c.set(Calendar.MONTH, 3);
			else if (currentMonth >= 7 && currentMonth <= 9)
				c.set(Calendar.MONTH, 4);
			else if (currentMonth >= 10 && currentMonth <= 12)
				c.set(Calendar.MONTH, 9);
			c.set(Calendar.DATE, 1);
			now = longSdf.parse(shortSdf.format(c.getTime()) + HH_mm_ss_START_TIME);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return now;
	}

	/**
	 * 当前季度的结束时间，如：2015-06-30 23:59:59
	 * 
	 * @return
	 */
	public static Date getCurrentQuarterEndTime() {
		Calendar c = Calendar.getInstance();
		int currentMonth = c.get(Calendar.MONTH) + 1;
		Date now = null;
		try {
			if (currentMonth >= 1 && currentMonth <= 3) {
				c.set(Calendar.MONTH, 2);
				c.set(Calendar.DATE, 31);
			} else if (currentMonth >= 4 && currentMonth <= 6) {
				c.set(Calendar.MONTH, 5);
				c.set(Calendar.DATE, 30);
			} else if (currentMonth >= 7 && currentMonth <= 9) {
				c.set(Calendar.MONTH, 8);
				c.set(Calendar.DATE, 30);
			} else if (currentMonth >= 10 && currentMonth <= 12) {
				c.set(Calendar.MONTH, 11);
				c.set(Calendar.DATE, 31);
			}
			now = longSdf.parse(shortSdf.format(c.getTime()) + HH_mm_ss_END_TIME);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return now;
	}

	/**
	 * 获取前/后半年的开始时间，如：2015-01-01 00:00:00
	 * 
	 * @return
	 */
	public static Date getHalfYearStartTime() {
		Calendar c = Calendar.getInstance();
		int currentMonth = c.get(Calendar.MONTH) + 1;
		Date now = null;
		try {
			if (currentMonth >= 1 && currentMonth <= 6) {
				c.set(Calendar.MONTH, 0);
			} else if (currentMonth >= 7 && currentMonth <= 12) {
				c.set(Calendar.MONTH, 6);
			}
			c.set(Calendar.DATE, 1);
			now = longSdf.parse(shortSdf.format(c.getTime()) + HH_mm_ss_START_TIME);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return now;
	}

	/**
	 * 获取前/后半年的结束时间，如：2015-06-30 23:59:59
	 * 
	 * @return
	 */
	public static Date getHalfYearEndTime() {
		Calendar c = Calendar.getInstance();
		int currentMonth = c.get(Calendar.MONTH) + 1;
		Date now = null;
		try {
			if (currentMonth >= 1 && currentMonth <= 6) {
				c.set(Calendar.MONTH, 5);
				c.set(Calendar.DATE, 30);
			} else if (currentMonth >= 7 && currentMonth <= 12) {
				c.set(Calendar.MONTH, 11);
				c.set(Calendar.DATE, 31);
			}
			now = longSdf.parse(shortSdf.format(c.getTime()) + HH_mm_ss_END_TIME);
		} catch (Exception e) {
			e.printStackTrace();

		}
		return now;
	}

	/**
	 * 获取当前月份天数
	 * 
	 * @return
	 */
	public static int getDaysOfMonth() {
		Calendar aCalendar = Calendar.getInstance();
		int day = aCalendar.getActualMaximum(Calendar.DATE);
		return day;
	}

	public static int daysBetween(Date smdate, Date bdate) {
		try {
			smdate = shortSdf.parse(shortSdf.format(smdate));
			bdate = shortSdf.parse(shortSdf.format(bdate));
			Calendar cal = Calendar.getInstance();
			cal.setTime(smdate);
			long time1 = cal.getTimeInMillis();
			cal.setTime(bdate);
			long time2 = cal.getTimeInMillis();
			long between_days = (time2 - time1) / (1000 * 3600 * 24);
			return Integer.parseInt(String.valueOf(between_days));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	public static int daysBetween(String smdate, String bdate) {
		try {
			Date s;
			Date b;
			s = shortSdf.parse(smdate);
			b = shortSdf.parse(bdate);
			Calendar cal = Calendar.getInstance();
			cal.setTime(s);
			long time1 = cal.getTimeInMillis();
			cal.setTime(b);
			long time2 = cal.getTimeInMillis();
			long between_days = (time2 - time1) / (1000 * 3600 * 24);
			return Integer.parseInt(String.valueOf(between_days));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	/**
	 * 判断当前时间是否在[startTime, endTime]区间 ,时间格式为"XX:XX:XX"
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static boolean isEffectiveDate(String startTime,int startPlusDay,String endTime,int endPlusDay){
		 Calendar calendarStart = Calendar.getInstance();
		 calendarStart.setTime(DateUtils.addDay(new Date(), startPlusDay));
		 calendarStart.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startTime.split(":")[0]));
		 calendarStart.set(Calendar.MINUTE, Integer.parseInt(startTime.split(":")[1]));
		 calendarStart.set(Calendar.SECOND, Integer.parseInt(startTime.split(":")[2]));
         Date  begin= calendarStart.getTime();
         
         Calendar calendarEnd = Calendar.getInstance();
         calendarEnd.setTime(DateUtils.addDay(new Date(), endPlusDay));
         calendarEnd.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endTime.split(":")[0]));
         calendarEnd.set(Calendar.MINUTE, Integer.parseInt(endTime.split(":")[1]));
         calendarEnd.set(Calendar.SECOND, Integer.parseInt(endTime.split(":")[2]));
         Date end = calendarEnd.getTime();
         if (new Date().after(begin) && new Date().before(end)) {
             return true;
         } else {
             return false;
         }
	}

    /**
     * 判断当前时间是否在[startTime, endTime]区间，注意时间格式要一致(拟定弃用)
     * 
     * @param nowTime 当前时间
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     * @author Leehq
     */
    public static boolean isEffectiveDates(Date nowTime, Date startTime, Date endTime) {
        if (nowTime.getTime() == startTime.getTime()
                || nowTime.getTime() == endTime.getTime()) {
            return true;
        }

        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }
	
    /**
     * 
     * @Title: isEffectiveDate   
     * @Description: TODO(判断当前时间是否在结束时间之前)   
     * @param: @param nowTime
     * @param: @param endTime
     * @param: @return      
     * @return: boolean      
     * @throws
     */
    public static boolean isBeforeDate(Date nowTime, Date endTime) {
        
        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (date.before(end)) {
            return true;
        } else {
            return false;
        }
    }
	
	/**
	 * 是否同一天
	 */
	public static boolean isSameDayDate(Date date1, Date date2) {
		if (longSdf.format(date1).equals(longSdf.format(date2))) {
			return true;
		} else {
			return false;
		}
	}

}
