package com.xiangxu.bikingjourney;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import com.google.gson.Gson;

import android.support.v7.app.ActionBarActivity;
import android.app.Fragment;
import android.content.Context;
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

public class NewJourneyActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_journey);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		

		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_journey, menu);
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
		
		Calendar startDate;
        Calendar endDate;
        int year;
        int month; 
        int day;
        private Button btnStartDate;
        private EditText etName;
        private Button btnEndDate;
        

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_new_journey,
					container, false);
			
			etName = (EditText)rootView.findViewById(R.id.et_name);
			
			final Calendar c = Calendar.getInstance();
	        year = c.get(Calendar.YEAR);
	        month = c.get(Calendar.MONTH); // Month is 0 based
	        day = c.get(Calendar.DAY_OF_MONTH);
	        
	        startDate = c;
	        endDate = c;
	   
	        	        
			btnStartDate = (Button)rootView.findViewById(R.id.btn_startdate);
			btnStartDate.setText(day + "/" + (month + 1) + "/" + year);
			
			btnStartDate.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					DatePickerFragment ddf = DatePickerFragment.newInstance(getActivity(), startDate);
					ddf.setDateDialogFragmentListener(new DateDialogFragmentListener() {
						 
					    @Override
					    public void dateDialogFragmentDateSet(Calendar date) {
					        // update the fragment
					    	startDate = date;
					    	year = startDate.get(Calendar.YEAR);
					    	month = startDate.get(Calendar.MONTH);
					    	day = startDate.get(Calendar.DAY_OF_MONTH);
					    	btnStartDate.setText(day + "/" + (month + 1) + "/" + year);
					    }
					});
					ddf.show(getFragmentManager(), "date picker dialog fragment");
				}
			});
			
			
			
			btnEndDate = (Button)rootView.findViewById(R.id.btn_enddate);
			btnEndDate.setText(day + "/" + (month + 1) + "/" + year);
			btnEndDate.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					DatePickerFragment ddf = DatePickerFragment.newInstance(getActivity(), endDate);
					ddf.setDateDialogFragmentListener(new DateDialogFragmentListener() {
						 
					    @Override
					    public void dateDialogFragmentDateSet(Calendar date) {
					        // update the fragment
					    	endDate = date;
					    	year = endDate.get(Calendar.YEAR);
					    	month = endDate.get(Calendar.MONTH);
					    	day = endDate.get(Calendar.DAY_OF_MONTH);
					    	btnEndDate.setText(day + "/" + (month + 1) + "/" + year);
					    }
					});
					ddf.show(getFragmentManager(), "date picker dialog fragment");
				}
			});
			
			Button btnSave = (Button)rootView.findViewById(R.id.btn_save_newjourney);
			btnSave.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String id = "J" + Integer.toString(GlobalData.journeyList.size() + 1);
					String name = etName.getText().toString();
					long milliSec1 = startDate.getTimeInMillis();
			        long milliSec2 = endDate.getTimeInMillis();
			        long timeDifInMilliSec = milliSec2 - milliSec1;
			        int timeDifDays = (int)timeDifInMilliSec / (24 * 60 * 60 * 1000) + 1;
			        
					
					Journey journey = new Journey(id, name, startDate, endDate, timeDifDays);
					GlobalData.journeyList.add(journey);
					
					// Convert the object to a JSON string
				    String json = new Gson().toJson(GlobalData.journeyList);
				    //Toast.makeText(getActivity(), json, Toast.LENGTH_SHORT).show();
				    
				    String filename = "journey.json";
				    FileOutputStream outputStream;

				    try {
				    	outputStream = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
				    	outputStream.write(json.getBytes());
				    	outputStream.close();
				    } catch (Exception e) {
				    	e.printStackTrace();
				    }
				    
				    Intent intent = new Intent(getActivity(), ToursActivity.class);
				    intent.putExtra("journey", new Gson().toJson(journey));
					startActivity(intent);
					getActivity().finish();
				}
			});
			return rootView;
		}
		
	}
		
}

