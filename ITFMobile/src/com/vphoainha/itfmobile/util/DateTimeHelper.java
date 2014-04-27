package com.vphoainha.itfmobile.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeHelper {

	public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	
	public static SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat(
			"MM-dd-yyyy");

	@SuppressWarnings("finally")
	public static Date stringToDateTime(String datetime) {
		@SuppressWarnings("deprecation")
		Date date = new Date(0000, 00, 00, 00, 00);
		try {
			date = simpleDateFormat.parse(datetime);
			return date;
		} catch (ParseException e) {
			e.printStackTrace();
		} finally {
			return date;
		}
	}

	public static String dateTimeToDateString(Date date) {
		String time = "0000-00-00 00:00:00";
		
		time = simpleDateFormat.format(date);
		return time;
	}
	
	public static String dateTimeToDateStringDMY(Date date) {
		String time = "00-00-0000";
		time = simpleDateFormat1.format(date);
		return time;
	}
}
