package com.xiangxu.bikingjourney;

import java.util.Calendar;

import com.google.api.client.util.DateTime;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.widget.DatePicker;

public class DatePickerFragment extends DialogFragment {
	
	public Context _context;
	public Calendar _date;
	static DateDialogFragmentListener _listener;
	
	public static DatePickerFragment newInstance(Context context, Calendar date)
    {
		DatePickerFragment dialog = new DatePickerFragment();
		dialog._context = context;
		dialog._date = date;
        return dialog;
    }

	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new DatePickerDialog(_context, dateSetListener, _date.get(Calendar.YEAR), _date.get(Calendar.MONTH), _date.get(Calendar.DAY_OF_MONTH));
        return dialog;
    }

	
	public void setDateDialogFragmentListener(DateDialogFragmentListener dateDialogFragmentListener){
		_listener = dateDialogFragmentListener;
	}
	
	private DatePickerDialog.OnDateSetListener dateSetListener =
		    new DatePickerDialog.OnDateSetListener() {
		 
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
		 
		                //create new Calendar object for date chosen
		                //this is done simply combine the three args into one
				Calendar newDate = Calendar.getInstance();
				newDate.set(year, monthOfYear, dayOfMonth);
				//call back to the DateDialogFragment listener
				_listener.dateDialogFragmentDateSet(newDate);
		 
			}
		};

}
