package com.xiangxu.bikingjourney;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.google.gson.Gson;

import android.app.Activity;
import android.app.ActionBar;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.os.Build;

public class NewTourActivity extends Activity {
	
	private static Journey journey;
	private static int dayNo;
	private static Tour tour;
	private static int fromOrTo = -1; // 0-from, 1-to

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_tour);

		String json = null;
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			json = extras.getString("journey");
			journey = new Gson().fromJson(json, Journey.class);
			dayNo = extras.getInt("dayNo");

			String tourId = "T" + Integer.toString(GlobalData.tourList.size() + 1);
			tour = new Tour(tourId);
			
			if (savedInstanceState == null) {
				getFragmentManager().beginTransaction()
						.add(R.id.container, new PlaceholderFragment()).commit();
			}
		}
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.new_tour, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		
		private Button btnMarkFrom;
		private Button btnDepartureTime;
		private Button btnArrivalTime;
		private Button btnUnmarkFrom;
		private Button btnMarkTo, btnUnmarkTo;
		private Button btnLogistic;

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_new_tour,
					container, false);
			btnMarkFrom = (Button) rootView.findViewById(R.id.btn_mark);
			btnUnmarkFrom = (Button) rootView.findViewById(R.id.btn_unmark);
			btnMarkTo = (Button) rootView.findViewById(R.id.btn_mark_to);
			btnUnmarkTo = (Button) rootView.findViewById(R.id.btn_unmark_to);
			btnDepartureTime = (Button) rootView.findViewById(R.id.btn_departuretime);
			btnArrivalTime = (Button) rootView.findViewById(R.id.btn_arrivaltime);
			btnLogistic = (Button) rootView.findViewById(R.id.btn_logistic);
			
			btnUnmarkFrom.setVisibility(View.INVISIBLE);
			btnUnmarkTo.setVisibility(View.INVISIBLE);
			
			final Calendar c = Calendar.getInstance();
	        int hour = c.get(Calendar.HOUR_OF_DAY);
	        int minute = c.get(Calendar.MINUTE);
	        
	        Calendar departureTime = c;
	        btnDepartureTime.setText(Utility.calendarToTime(departureTime));
	        
	        Calendar arrivalTime = c;
	        arrivalTime.set(Calendar.HOUR_OF_DAY, hour + 1);
	        
	        
	        btnArrivalTime.setText(Utility.calendarToTime(arrivalTime));
	        
	        
	        Calendar startDate = journey.getStartDate();
	        int year = startDate.get(Calendar.YEAR);
	        int month = startDate.get(Calendar.MONTH);
	        int day = startDate.get(Calendar.DAY_OF_MONTH);
	        
	        btnMarkFrom.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					fromOrTo = 0;
					Intent intent = new Intent(getActivity(), MapPlanActivity.class);
					intent.putExtra("tourId", tour.getTourId());
					if (tour.getFrom() != null)
						MapPlanActivity.savedPlace = tour.getFrom();
					else
						MapPlanActivity.savedPlace = null;
					startActivity(intent);
				}
			});
	        
	        btnMarkTo.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					fromOrTo = 1;
					Intent intent = new Intent(getActivity(), MapPlanActivity.class);
					intent.putExtra("tourId", tour.getTourId());
					if (tour.getTo() != null)
						MapPlanActivity.savedPlace = tour.getTo();
					else
						MapPlanActivity.savedPlace = null;
					startActivity(intent);
				}
			});
	        	        
			btnDepartureTime.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					SimpleDateFormat curFormater = new SimpleDateFormat("HH:mm"); 
			        Date dateObj = null;
					try {
						dateObj = curFormater.parse(btnDepartureTime.getText().toString());
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
					Calendar calendar = Calendar.getInstance();
			        calendar.setTime(dateObj);
					DialogFragment newFragment = TimePickerFragment.newInstance(calendar, btnDepartureTime);
		            newFragment.show(getActivity().getFragmentManager(), "timePicker");
				}
			});
			
			btnArrivalTime.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					String time = btnArrivalTime.getText().toString();
					DialogFragment newFragment = TimePickerFragment.newInstance(Utility.timeToCalendar(time), btnArrivalTime);
		            newFragment.show(getActivity().getFragmentManager(), "timePicker");
				}
			});
			
			btnLogistic.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), CategoryActivity.class);
					startActivity(intent);
				}
			});
			
			return rootView;
		}
		
		@Override
		public void onResume() {
			super.onResume();
			if (MapPlanActivity.savedPlace != null) {
				if (fromOrTo == 0) {
					btnMarkFrom.setText(MapPlanActivity.savedPlace.getName());
					tour.setFrom(MapPlanActivity.savedPlace);
					btnUnmarkFrom.setVisibility(View.VISIBLE);
				} else if (fromOrTo == 1) {
					btnMarkTo.setText(MapPlanActivity.savedPlace.getName());
					tour.setTo(MapPlanActivity.savedPlace);
					btnUnmarkTo.setVisibility(View.VISIBLE);
				}
			}
		}
	}
}