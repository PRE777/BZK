package com.iwhere.gridgeneration.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 时间值
 * @author niucaiyun
 *
 */
public class DateUtils {

	private static final DateFormat DATE_TIME = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final DateFormat DATE_TIME1 = new SimpleDateFormat("yyyyMMddHHmmss");
	private static final DateFormat DATE = new SimpleDateFormat("yyyy-MM-dd");
//	private static final DateFormat MONTH_DAY = new SimpleDateFormat("MM月dd日");
	
	public static Integer getHour() {
		Calendar instance = Calendar.getInstance();
		instance.setTimeZone(TimeZone.getDefault());
		int i = instance.get(Calendar.HOUR_OF_DAY);
		return i;
	}
	
	public static Long parseTime(String dateTime) throws ParseException {
		Date parse = DATE_TIME.parse(dateTime);
		return parse.getTime();
	}

	public static Date parseTimeDate(String dateTime) throws ParseException {
		Date parse = DATE_TIME.parse(dateTime);
		return parse;
	}
	
	public static Long parseTime1(String dateTime) throws ParseException {
		Date parse = DATE_TIME1.parse(dateTime);
		return parse.getTime();
	}
	
	public static String getDateTime() {
		return DATE_TIME.format(new Date());
	}
	public static String getDateTime(Long millions) {
		return DATE_TIME.format(millions);
	}
	
	public static String getDate() {
		return DATE.format(new Date());
	}
	public static String getDate(Long millions) {
		return DATE.format(millions);
	}
	
	public static String getMonthDay() {
		Calendar instance = Calendar.getInstance();
		int month = instance.get(Calendar.MONTH);
		int day = instance.get(Calendar.DAY_OF_MONTH);
		return (month + 1) + "月" + day + "日";
	}
	
	public static String getTime(String prefix) {
		DateFormat format = new SimpleDateFormat(prefix);
		return format.format(new Date());
	}
	public static String getTime(String prefix, Long millions) {
		DateFormat format = new SimpleDateFormat(prefix);
		return format.format(new Date(millions));
	}
	
}
