package com.alibaba.util;

import java.util.Calendar;
import java.util.Date;

public class TimeUtil {

	/**
	 * 获取long当前时间
	 * 
	 * @return
	 */
	public static Long getNowTime() {
		Date today = new Date();
		long nowtime = today.getTime();
		return nowtime;
	}

	/**
	 * 获取long时间
	 * 
	 * @param timeType
	 *            Calendar.
	 * @param value
	 *            -1
	 * @return
	 */
	public static Long getTime(int timeType, int value) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(timeType, value);
		Date date = calendar.getTime();
		return date.getTime();
	}

	/**
	 * 获取某天开始时间戳
	 * 
	 * @param day
	 * @return
	 */
	public static Long getDayStartTime(int day) {
		Calendar cal = Calendar.getInstance();
		Date date = new Date();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime().getTime() + day * 24 * 60 * 60 * 1000;
	}

	/**
	 * 获取某天结束时间戳
	 * 
	 * @param day
	 * @return
	 */
	public static Long getDayEndTime(int day) {
		Calendar cal = Calendar.getInstance();
		Date date = new Date();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		return cal.getTime().getTime() + day * 24 * 60 * 60 * 1000;
	}

}
