package com.custom.widget.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class StringUtil {
	public static final String DD_MMM_YYYY 					= "dd/MM/yyyy";
	public static final DateFormat DATEFORMAT_DD_MMM_YYYY = new SimpleDateFormat(DD_MMM_YYYY);

	public static final String DD_MMM_YYYY_HH_MM_SS_WITHCOLON 	= "dd-MMM-yyyy HH:mm:ss";
	public static final String YY_MM_DD_HH_MM_SS 			= "dd-MMM-yyyy HH:mm:ss";
	public static final DateFormat DATEFORMAT_YY_MM_DD_HH_MM_SS = new SimpleDateFormat(YY_MM_DD_HH_MM_SS);
	public static final DateFormat DATEFORMAT_DD_MMM_YYYY_HH_MM_SS_WITHCOLON = new SimpleDateFormat(DD_MMM_YYYY_HH_MM_SS_WITHCOLON);

	
	public static boolean isNullOrEmpty(String str){
		if(str != null && str.length()>0 ){
			return false;
		}else{
			return true;
		}
	}
	public static boolean isNotNullOrEmpty(String str){
		return !isNullOrEmpty(str);
	}
	public static String generateRandomInteger(){
		UUID randomnumber = UUID.randomUUID();
		return randomnumber.toString();
	}
	
	public static Date getDateWithStartTime(Date date){
		Date newDate = null;
		if(date!=null){
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal = getCalendarWithStartTime(cal);
		    newDate = cal.getTime();
		}
		return newDate;
	}
	public static Date getDateWithEndTime(Date date){
		Date newDate = null;
		if(date!=null){
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal = getCalendarWithEndTime(cal);
		    newDate = cal.getTime();
		}
		return newDate;
	}
	
	public static Calendar getCalendarWithStartTime(Calendar cal){
		cal.set(Calendar.HOUR_OF_DAY, 0);
	    cal.set(Calendar.MINUTE, 0);
	    cal.set(Calendar.SECOND, 0);
	    cal.set(Calendar.MILLISECOND,0);
		return cal;
	}
	
	public static Calendar getCalendarWithEndTime(Calendar cal){
		cal.set(Calendar.HOUR_OF_DAY, 23);
	    cal.set(Calendar.MINUTE, 59);
	    cal.set(Calendar.SECOND, 59);
	    cal.set(Calendar.MILLISECOND,0);
		return cal;
	}
	
	public static String parseDateWithTime(Date date) {
		 
		String date2 = null; 
		DateFormat formatter ; 
		formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		//formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		date2 = (String) formatter.format(date);  
		 
		return date2;
	}
	
	public static String getTodayDate(){
		String date = DATEFORMAT_DD_MMM_YYYY.format(getNewDateWithCurrentTime());
		return date;
	}
	public static Date getNewDateWithCurrentTime(){
		Calendar calendar = Calendar.getInstance();
		return calendar.getTime();
	}
	
	public static Date getTimeAfterXSeconds(int x) {
		Calendar today = Calendar.getInstance();
		today.add(Calendar.SECOND, +x);
		Date date = today.getTime();
		return date;
	}
	
	public static boolean isTodayOrFutureDateWithTime(Date date) {
		boolean isTodayOrFutureDay = false;
		Date today = getTodayDateWithTime();
		if(date.getTime() >= today.getTime()){
			isTodayOrFutureDay = true;
	}
		return isTodayOrFutureDay;
		
	}
	
	public static Date getTodayDateWithTime() {
		String date = StringUtil.DATEFORMAT_YY_MM_DD_HH_MM_SS.format(getNewDateWithCurrentTime());
		return parseString(date, StringUtil.DD_MMM_YYYY_HH_MM_SS_WITHCOLON);
	}
	
	public static Date parseString(String dateString, String format) {
		Date date = null;
		DateFormat formatter = null;
		if(StringUtil.isNotNullOrEmpty(dateString) && StringUtil.isNotNullOrEmpty(format)){
			try {
				formatter = new SimpleDateFormat(format);
				date = (Date) formatter.parse(dateString);
			} catch (ParseException e) {
				System.out.println("Exception :" + e);
			}
		}
		return date;
	}
	
	
}
