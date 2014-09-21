package com.xiangxu.bikingjourney;

import java.net.URL;
import java.util.HashMap;
import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.os.Build;

public class PlaceListActivity extends ActionBarActivity {
	
	ConnectionDetector cd;
	Boolean isInternetPresent = false;
	AlertDialogManager alert = new AlertDialogManager();
	ProgressDialog pDialog;
	GooglePlaces googlePlaces;
	PlaceList nearPlaces;
	GPSTracker gps;
	HashMap<String, Bitmap> places = new HashMap<String, Bitmap>();
	HashMap<String, Float> distances = new HashMap<String, Float>();
	String types;
	LatLng currentPosition;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_place_list);
		
		types = getIntent().getStringExtra("types");
		
		Button btnShowMap = (Button)findViewById(R.id.btn_show_place_map);
        btnShowMap.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), PlacesMapActivity.class);
				intent.putExtra("near_places", nearPlaces);
				startActivity(intent);
			}
		});
		
		cd = new ConnectionDetector(getApplicationContext());
		
		// Check if Internet present
        isInternetPresent = cd.isConnectingToInternet();
        if (!isInternetPresent) {
            // Internet Connection is not present
            alert.showAlertDialog(PlaceListActivity.this, "Internet Connection Error",
                    "Please connect to working Internet connection", false);
            // stop executing code by return
            return;
        }
        
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
        
        if(status!=ConnectionResult.SUCCESS){ // Google Play Services are not available
	    	int requestCode = 10;
	    	Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
	    	dialog.show();
	    }
	    else
	    {
	        
	        // Getting LocationManager object from System Service LOCATION_SERVICE
	        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
	        
	        // Creating a criteria object to retrieve provider
	        Criteria criteria = new Criteria();	        
	        // Getting the name of the best provider
	        String provider = locationManager.getBestProvider(criteria, true);
	        // Getting Current Location
	        Location mlocation = locationManager.getLastKnownLocation(provider);
	        currentPosition = new LatLng(mlocation.getLatitude(),mlocation.getLongitude());
	        
	        
	        LocationListener locationListener = new LocationListener()
	        {

				@Override
				public void onLocationChanged(Location location) {
					currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
					//new LoadPlaces().execute();
				}

				@Override
				public void onProviderDisabled(String provider) {
					// do nothing		
				}

				@Override
				public void onProviderEnabled(String provider) {
					// do nothing
				}

				@Override
				public void onStatusChanged(String provider, int status,
						Bundle extras) {
					// do nothing
				}
	        	
	        };
		    locationManager.requestLocationUpdates(provider, 20000, 0, locationListener);
        
        new LoadPlaces().execute();
	    }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.place_list, menu);
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
	
	class LoadPlaces extends AsyncTask<String, String, String> {
		 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(PlaceListActivity.this);
            pDialog.setMessage(Html.fromHtml("<b>Search</b><br/>Loading Places..."));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        /**
         * getting Places JSON
         * */
        protected String doInBackground(String... args) {
            // creating Places class object
            googlePlaces = new GooglePlaces();
             
            try {
                 
                // Radius in meters - increase this value if you don't find any places
                double radius = 1000; // 1000 meters
                 
                // get nearest places
                nearPlaces = googlePlaces.search(currentPosition.latitude,
                		currentPosition.longitude, radius, types);
                
                String status = nearPlaces.status;
                if(status.equals("OK")){
                    // Successfully got places details
                    if (nearPlaces.results != null) {
                			for (Place place : nearPlaces.results) {
                				String photo_reference = place.photos.get(0).photo_reference;
                				Bitmap bitmap = googlePlaces.getPlacePhoto(photo_reference);
                				if (bitmap == null)	{
                					String icon = place.icon;
                					URL newurl = new URL(icon); 
                					bitmap = BitmapFactory.decodeStream(newurl.openConnection() .getInputStream());
                				}
                				places.put(place.place_id, bitmap);
                			}
                    }
                }
                 
 
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
 
        /**
         * After completing background task Dismiss the progress dialog
         * and show the data in UI
         * Always use runOnUiThread(new Runnable()) to update UI from background
         * thread, otherwise you will get error
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed Places into LISTVIEW
                     * */
                    // Get json response status
                    String status = nearPlaces.status;
                     
                    // Check for all possible status
                    if(status.equals("OK")){
                        // Successfully got places details
                        if (nearPlaces.results != null) {
                    			getFragmentManager().beginTransaction()
                    					.add(R.id.container, new PlaceListFragment().newInstance(getApplicationContext(), nearPlaces.results, places, currentPosition)).commit();
                        }
                    }
                    else if(status.equals("ZERO_RESULTS")){
                        // Zero results found
                        alert.showAlertDialog(PlaceListActivity.this, "Near Places",
                                "Sorry no places found. Try to change the types of places",
                                false);
                    }
                    else if(status.equals("UNKNOWN_ERROR"))
                    {
                        alert.showAlertDialog(PlaceListActivity.this, "Places Error",
                                "Sorry unknown error occured.",
                                false);
                    }
                    else if(status.equals("OVER_QUERY_LIMIT"))
                    {
                        alert.showAlertDialog(PlaceListActivity.this, "Places Error",
                                "Sorry query limit to google places is reached",
                                false);
                    }
                    else if(status.equals("REQUEST_DENIED"))
                    {
                        alert.showAlertDialog(PlaceListActivity.this, "Places Error",
                                "Sorry error occured. Request is denied",
                                false);
                    }
                    else if(status.equals("INVALID_REQUEST"))
                    {
                        alert.showAlertDialog(PlaceListActivity.this, "Places Error",
                                "Sorry error occured. Invalid Request",
                                false);
                    }
                    else
                    {
                        alert.showAlertDialog(PlaceListActivity.this, "Places Error",
                                "Sorry error occured.",
                                false);
                    }
                }
            });
 
        }
 
    }

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_place_list,
					container, false);
			return rootView;
		}
	}

}
