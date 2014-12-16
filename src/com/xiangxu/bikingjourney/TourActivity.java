package com.xiangxu.bikingjourney;

import java.util.Calendar;

import com.google.gson.Gson;
import com.xiangxu.bikingjourney.database.TourDataSource;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

public class TourActivity extends ActionBarActivity {
	
	private Tour tour;
	private TourDataSource datasource;
	
	private Button btnMarkFrom;
	private Button btnMarkTo;
	private Button btnDepartureTime;
	private Button btnArrivalTime;
	private RatingBar rtbDifficulty;
	private EditText etRemark;
	private AlertDialogManager alert = new AlertDialogManager();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tour);
		
		// get intent values
		String tourJson = getIntent().getStringExtra("tour");
		long journeyId = getIntent().getLongExtra("journeyId", 0);
		int dayNo = getIntent().getIntExtra("dayNo", 0);
		
		// initialize widgets
		btnMarkFrom = (Button)findViewById(R.id.btn_mark);
		Button btnUnmarkFrom = (Button)findViewById(R.id.btn_unmark);
		btnMarkTo = (Button)findViewById(R.id.btn_mark_to);
		Button btnUnmarkTo = (Button)findViewById(R.id.btn_unmark_to);
		btnDepartureTime = (Button)findViewById(R.id.btn_departuretime);
		btnArrivalTime = (Button)findViewById(R.id.btn_arrivaltime);
		rtbDifficulty = (RatingBar)findViewById(R.id.rb_difficulty);
		etRemark = (EditText)findViewById(R.id.et_remark);
		Button btnSave = (Button)findViewById(R.id.btn_save_tour);
		
		
		if (tourJson != null) { // edit
			tour = new Gson().fromJson(tourJson, Tour.class);
			// set widgets
			btnMarkFrom.setText(tour.getFrom().getName());
			btnMarkTo.setText(tour.getTo().getName());
			btnDepartureTime.setText(tour.getDepartureTime());
			btnArrivalTime.setText(tour.getArrivalTime());
			rtbDifficulty.setRating(tour.getDifficulty());
			etRemark.setText(tour.getRemark());
		} else {	// new
			tour = new Tour(journeyId, dayNo);
			Calendar departureTime = Calendar.getInstance();
			btnDepartureTime.setText(Utility.calendarToTime(departureTime));
			Calendar arrivalTime = departureTime;
			arrivalTime.set(Calendar.HOUR_OF_DAY, departureTime.get(Calendar.HOUR_OF_DAY) + 1);
			btnArrivalTime.setText(Utility.calendarToTime(arrivalTime));
		}
		
		datasource = new TourDataSource(this);
	    datasource.open();
		
		
		
		
		
		// set widgets event
		btnMarkFrom.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onClickMarkFromButton();
			}
		});
		btnMarkTo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onClickMarkToButton();
			}
		});
		btnUnmarkFrom.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onClickUnmarkFromButton();
			}
		});
		btnUnmarkTo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onClickUnmarkToButton();
			}
		});
		btnDepartureTime.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onClickDepartureTimeButton();
			}
		});
		btnArrivalTime.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onClickArrivalTimeButton();
			}
		});
		btnSave.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onClickSaveButton();
			}
		});
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
	
	private void onClickMarkFromButton() {
		Intent intent = new Intent(this, MapPlanActivity.class);
		if (tour.getFrom() != null)
			intent.putExtra("marked_location", new Gson().toJson(tour.getFrom()));
		startActivityForResult(intent, GlobalData.FROM_SAVED);
	}
	
	private void onClickMarkToButton() {
		Intent intent = new Intent(this, MapPlanActivity.class);
		intent.putExtra("marked_location", new Gson().toJson(tour.getTo()));
		startActivityForResult(intent, GlobalData.TO_SAVED);
	}
	
	private void onClickUnmarkFromButton() {
		tour.setFrom(null);
		btnMarkFrom.setText("mark on map");
	}
	
	private void onClickUnmarkToButton() {
		tour.setTo(null);
		btnMarkTo.setText("mark on map");
	}
	
	private void onClickDepartureTimeButton() {      
        String time = btnDepartureTime.getText().toString();
		DialogFragment newFragment = TimePickerFragment.newInstance(Utility.timeToCalendar(time), btnDepartureTime);
        newFragment.show(this.getFragmentManager(), "timePicker");
	}
	
	private void onClickArrivalTimeButton() {
		String time = btnArrivalTime.getText().toString();
		DialogFragment newFragment = TimePickerFragment.newInstance(Utility.timeToCalendar(time), btnArrivalTime);
        newFragment.show(this.getFragmentManager(), "timePicker");
	}
	
	private void onClickSaveButton() {
		// check departure and destination places
		if (tour.getFrom() == null || tour.getTo() == null) {
			alert.showAlertDialog(getApplicationContext(), "Data invalid"
					, "Form or To can not be null!", false);
			return;
		}
		
		// check time
		Calendar departureTime = Utility.timeToCalendar(btnDepartureTime.getText().toString());
		Calendar arrivalTime = Utility.timeToCalendar(btnArrivalTime.getText().toString());
		if (arrivalTime.before(departureTime)) {
			alert.showAlertDialog(getApplicationContext(), "Data invalid", "Arrival Time must be the same with or after Departure Time!", false);
			return;
		}
		tour.setDepartureTime(btnDepartureTime.getText().toString());
		tour.setArrivalTime(btnArrivalTime.getText().toString());
		tour.setDiffidulty((int)rtbDifficulty.getRating());
		tour.setRemark(etRemark.getText().toString());
		long tourId = tour.getTourId();
		if (tourId != 0)
			datasource.updateTour(tour);
		else
			tourId = datasource.createTour(tour);
		
		// prepare json string
		String s = new Gson().toJson(tour);
	    Intent returnIntent = new Intent();
		returnIntent.putExtra("tour_updated",s);
		setResult(RESULT_OK,returnIntent);
		
		// go to LogisticsActivity
		Intent intent = new Intent(getApplicationContext(), LogisticsActivity.class);
	    intent.putExtra("tourId", tourId);
		startActivity(intent);
		
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.tour, menu);
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
	
	public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
		if (resultCode == RESULT_OK) {
			 String json = data.getStringExtra("SavedPlace");
			 TourPlace savedPlace = new Gson().fromJson(json, TourPlace.class);
			 switch(requestCode) {
			    case GlobalData.FROM_SAVED:
			    	tour.setFrom(savedPlace);
					btnMarkFrom.setText(savedPlace.getName());
			        break;
			    case GlobalData.TO_SAVED:
			    	tour.setTo(savedPlace);
			    	btnMarkTo.setText(savedPlace.getName());
			    	break;
			    }
			 

		 }		 
    }  
}
