package com.xiangxu.bikingjourney;

import java.util.Calendar;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.Button;
import android.widget.TimePicker;

public class TimePickerFragment extends DialogFragment
									implements TimePickerDialog.OnTimeSetListener {
	
	private Calendar time;
	private Button button;
	
	public void setTime(Calendar time) {
		this.time = time;
	}
	
	public void setButton(Button btn) {
		this.button = btn;
	}
	
	public TimePickerFragment() {
		
	}
	
	public static TimePickerFragment newInstance (Calendar time, Button btn) {
		TimePickerFragment fragment = new TimePickerFragment();
		fragment.setTime(time);
		fragment.setButton(btn);
		return fragment;
	}
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int hour = time.get(Calendar.HOUR_OF_DAY);
        int minute = time.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
    	String s = null;
    	if (hourOfDay < 10)
    		s = "0" + Integer.toString(hourOfDay);
    	else
    		s = Integer.toString(hourOfDay);
    	s = s + ":";
    	if (minute < 10)
    		s = s + "0" + minute;
    	else
    		s = s + Integer.toString(minute);
        button.setText(s);
    }


}

