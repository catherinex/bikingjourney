/*
 * for json file
 */

package com.xiangxu.bikingjourney;

import java.util.Calendar;

public class Journey {
	
	private String _id;
	private String _name;
	private Calendar _startDate;
	private Calendar _endDate;
	private int _nDays;
	
	public void setId(String id) {
		_id = id;
	}
	
	public int getNDays() {
		return _nDays;
	}
	
	public Calendar getStartDate() {
		return _startDate;
	}
	
	public Journey (String id, String name, Calendar startDate, Calendar endDate, int days) {
		_id = id;
		_name = name;
		_startDate = startDate;
		_endDate = endDate;
		_nDays = days;
	}

}
