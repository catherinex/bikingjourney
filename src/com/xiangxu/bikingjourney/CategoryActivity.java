package com.xiangxu.bikingjourney;

import java.util.ArrayList;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.os.Build;

public class CategoryActivity extends ActionBarActivity
	implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {
	
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	private static final long UPDATE_INTERVAL = 5000;

	private static final long FASTEST_INTERVAL = 1000;
	
	LocationRequest locationRequest;
	LocationClient locationClient;
	Location myLocation;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_category);
		
		boolean isNearbyMe = getIntent().getBooleanExtra("isNearbyMe", false);

		if (savedInstanceState == null) {
			ArrayList<Option> options = GlobalData.getLogisticCategoryList();
			
			getFragmentManager().beginTransaction()
					.add(R.id.container_category, new CategoryFragment().newInstance(this, options, isNearbyMe)).commit();
		}
		
		 locationClient = new LocationClient(this, this, this);
		 locationRequest = new LocationRequest();
		 // Use high accuracy
		 locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		 // Set the update interval to 5 seconds
		 locationRequest.setInterval(UPDATE_INTERVAL);
		 // Set the fastest update interval to 1 second
		 locationRequest.setFastestInterval(FASTEST_INTERVAL);
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
		getMenuInflater().inflate(R.menu.category, menu);
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
	
	@Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        // Decide what to do based on the original request code
        switch (requestCode) {
            case CONNECTION_FAILURE_RESOLUTION_REQUEST :
            /*
             * If the result code is Activity.RESULT_OK, try
             * to connect again
             */
                switch (resultCode) {
                    case Activity.RESULT_OK :
                    /*
                     * Try the request again
                     */
                    break;
                }
        }
     }

	@Override
	public void onLocationChanged(Location location) {
		 locationClient.removeLocationUpdates(this);
		 myLocation = location;
		//Toast.makeText(this, "Location: " + location.getLatitude() + ", " + location.getLongitude(), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            //showErrorDialog(connectionResult.getErrorCode());
        }

	}

	@Override
	public void onConnected(Bundle bundle) {
		myLocation = locationClient.getLastLocation();
		if (myLocation == null)
		locationClient.requestLocationUpdates(locationRequest, this);
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}


}
