package com.xiangxu.bikingjourney;

import java.util.Calendar;

import com.google.gson.Gson;
import com.xiangxu.bikingjourney.database.JourneyDataSource;

import android.support.v7.app.ActionBarActivity;
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

public class NewJourneyActivity extends ActionBarActivity {
	
	private Journey journey;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_journey);
		
		
		
		String json = getIntent().getStringExtra("journey");
		if (json != null)
			journey = new Gson().fromJson(json, Journey.class);
		

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment(journey)).commit();
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
        private JourneyDataSource datasource;
        private Journey journey;
        private AlertDialogManager alert = new AlertDialogManager();
        

		public PlaceholderFragment(Journey journey) {
			this.journey = journey;
		}


		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			
			datasource = new JourneyDataSource(getActivity());
		    datasource.open();
		    
			View rootView = inflater.inflate(R.layout.fragment_new_journey,
					container, false);
			
			etName = (EditText)rootView.findViewById(R.id.et_name);
			btnStartDate = (Button)rootView.findViewById(R.id.btn_startdate);
			btnEndDate = (Button)rootView.findViewById(R.id.btn_enddate);
			
			// set widgets
			if (journey == null) {
				final Calendar c = Calendar.getInstance();
				year = c.get(Calendar.YEAR);
				month = c.get(Calendar.MONTH); // Month is 0 based
				day = c.get(Calendar.DAY_OF_MONTH);
	        
				startDate = c;
				endDate = c;
	   
	        	btnStartDate.setText(year + "-" + (month+1) + "-" + day);        
	        	btnEndDate.setText(year + "-" + (month+1) + "-" + day);  
			} else {
				startDate = journey.getStartDate();
				endDate = journey.getEndDate();
				etName.setText(journey.getName());
				btnStartDate.setText(Utility.calendarToDate(startDate));
				btnEndDate.setText(Utility.calendarToDate(endDate));
				
			}
			
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
					    	btnStartDate.setText(year + "-" + (month+1) + "-" + day);  
					    }
					});
					ddf.show(getFragmentManager(), "date picker dialog fragment");
				}
			});
			
			
			
			
			
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
					    	btnEndDate.setText(year + "-" + (month+1) + "-" + day);
					    }
					});
					ddf.show(getFragmentManager(), "date picker dialog fragment");
				}
			});
			
			Button btnSave = (Button)rootView.findViewById(R.id.btn_save_newjourney);
			
			// click save and next step button
			btnSave.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// check name
					String name = etName.getText().toString();
					if (name.equals("")) {
						alert.showAlertDialog(getActivity(), "Data invalid",
                                "Please input the name!",
                                false);
						return;
					}
					
					// check date
					if (endDate.before(startDate)) {
						alert.showAlertDialog(getActivity(), "Data invalid", "End Date must be same with or after Start Date!", false);
						return;
					}
					long milliSec1 = startDate.getTimeInMillis();
			        long milliSec2 = endDate.getTimeInMillis();
			        long timeDifInMilliSec = milliSec2 - milliSec1;
			        int timeDifDays = (int)timeDifInMilliSec / (24 * 60 * 60 * 1000) + 1;
				    
				    // save to database
			        if (journey == null)
			        	journey = datasource.createJourney(name, startDate, endDate, timeDifDays);
			        else {
			        	journey.setName(name);
			        	journey.setStartDate(startDate);
			        	journey.setEndDate(endDate);
			        	journey.setNDays(timeDifDays);
			        	datasource.updateJourney(journey.getId(), name, startDate, endDate, timeDifDays);
			        }
			        
			     // prepare json string
					String s = new Gson().toJson(journey);
				    Intent returnIntent = new Intent();
					returnIntent.putExtra("journey_saved",s);
					getActivity().setResult(RESULT_OK,returnIntent);
				    
				    // go to ToursActivity
				    Intent intent = new Intent(getActivity(), ToursActivity.class);
				    intent.putExtra("journey", new Gson().toJson(journey));
					startActivity(intent);
					getActivity().finish();
				}
			});
			return rootView;
		}
		
		@Override
		public void onResume() {
		    datasource.open();
		    super.onResume();
		  }

		  @Override
		public void onPause() {
		    datasource.close();
		    super.onPause();
		  }
		
	}
		
}

