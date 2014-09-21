package com.xiangxu.bikingjourney;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import android.app.Fragment;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;
import android.os.Build;

public class NearbyActivity extends ActionBarActivity
implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {
	
	LocationRequest locationRequest;
	LocationClient locationClient;
	Location myLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nearby);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		Button btnSearch = (Button)findViewById(R.id.btn_search_nearby);
		
		btnSearch.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), ShowPlaceActivity.class);
				startActivity(intent);
				
			}
		});
		
		locationClient = new LocationClient(this, this, this);
		locationRequest = new LocationRequest();
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	}
	
	@Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        locationClient.connect();
    }
	
	@Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
		locationClient.disconnect();
        super.onStop();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.nearby, menu);
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
		
		AutoCompleteTextView actvNearby;

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_nearby,
					container, false);
			RadioGroup rgNearby = (RadioGroup)rootView.findViewById(R.id.rg_nearby);
			actvNearby = (AutoCompleteTextView)rootView.findViewById(R.id.actv_searchlocation);
			
			rgNearby.check(R.id.rb_currentlocation);
			actvNearby.setEnabled(false);
			actvNearby.setAdapter(new PlacesAutoCompleteAdapter(getActivity(), R.layout.place_auto_list_item));

			
			rgNearby.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					switch (checkedId) {
						case R.id.rb_currentlocation:
							actvNearby.setText("");
							actvNearby.setEnabled(false);
							break;
						case R.id.rb_searchlocation:
							actvNearby.setEnabled(true);
					}
				}
				
			});
			
			actvNearby.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					GooglePlaceAutocompletePrediction selected = (GooglePlaceAutocompletePrediction) arg0.getAdapter().getItem(arg2);
		            Toast.makeText(getActivity(),
		                    "Clicked " + arg2 + " name: " + selected.description,
		                    Toast.LENGTH_SHORT).show();
				}
				
			});
			
			GooglePlaces googlePlaces = new GooglePlaces();

			return rootView;
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		locationClient.removeLocationUpdates(this);
		myLocation = location;
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle arg0) {
		myLocation = locationClient.getLastLocation();
		if (myLocation == null)
		locationClient.requestLocationUpdates(locationRequest, this);
		Toast.makeText(this, "Location: " + myLocation.getLatitude() + ", " + myLocation.getLongitude(), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}

}
