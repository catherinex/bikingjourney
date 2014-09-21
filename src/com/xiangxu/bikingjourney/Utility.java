package com.xiangxu.bikingjourney;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utility {
	
	public static String calendarToTime(Calendar calendar) {
		String result = null;
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		if (hour < 10)
			result = "0" + hour;
        else
        	result = Integer.toString(hour);
		result = result + ":";
        if (minute < 10)
        	result = result + "0" + minute;
        else
        	result = result + Integer.toString(minute);
		return result;
	}
	
	public static Calendar timeToCalendar(String time) {
		SimpleDateFormat curFormater = new SimpleDateFormat("HH:mm"); 
        Date dateObj = null;
		try {
			dateObj = curFormater.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		} 
		Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateObj);
        return calendar;
	}

}
