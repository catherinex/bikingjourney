package com.xiangxu.bikingjourney;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.xiangxu.bikingjourney.ShowPlaceActivity.LoadPlaces;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;
import android.database.Cursor;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Html;

@SuppressLint("DefaultLocale")
public class MapPlanActivity extends ActionBarActivity
                             implements LoaderCallbacks<Cursor>
								, InputDialogFragment.InputDialogListener
								, LayersDialogFragment.LayersDialogListener
{
	
	GoogleMap googleMap;
	SearchView searchView;
	Button btnSave;
	Button btnClear;
	LatLng currentLocation; // current location
	ProgressDialog pDialog;
	String type;
	LatLng userLocation;
	String tourId;
	
	AlertDialogManager alert = new AlertDialogManager();
	
	private TourPlace searchedPlace = null; // searched place
	
	public static TourPlace savedPlace = null; // saved place
	
	HashMap<Marker, Place> hmPlace = new HashMap<Marker, Place>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_plan);
		
		btnSave = (Button)findViewById(R.id.btn_save_location);
		btnClear = (Button)findViewById(R.id.btn_clear_map);
		
		tourId = getIntent().getStringExtra("tourId");
		
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
		if(status!=ConnectionResult.SUCCESS){ // Google Play Services are not available
	    	int requestCode = 10;
	    	Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
	    	dialog.show();
		}
		else {
			MapFragment fm = (MapFragment) getFragmentManager().findFragmentById(R.id.map_plan);
			googleMap = fm.getMap();
			googleMap.setMyLocationEnabled(true);
			
			GPSTracker gps = new GPSTracker(this);
        	if (!gps.canGetLocation()) {
                // Can't get user's current location
                alert.showAlertDialog(MapPlanActivity.this, "GPS Status",
                        "Couldn't get location information. Please enable GPS",
                        false);
                // stop executing code by return
                return;
            }
        	Double user_latitude = gps.getLatitude();
        	Double user_longitude = gps.getLongitude();
        	currentLocation = new LatLng(user_latitude, user_longitude);
        	
        	
        	if (savedPlace == null) {
        		userLocation = new LatLng(user_latitude, user_longitude);
        		btnClear.setEnabled(false);
        	}
        	else {
            	userLocation = savedPlace.getLocation();
            	MarkerOptions marker = new MarkerOptions().position(userLocation).title(savedPlace.getName());
                googleMap.addMarker(marker);
                btnSave.setEnabled(false);
        	}
            
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,14));
            
            /*googleMap.setInfoWindowAdapter(new InfoWindowAdapter() {

				@Override
				public View getInfoContents(Marker marker) {
					View myContentView = getLayoutInflater().inflate(
		                    R.layout.info_window, null);
					myContentView.setLayoutParams(new RelativeLayout.LayoutParams(500, 500));
		            TextView tvTitle = ((TextView) myContentView
		                    .findViewById(R.id.tv_name_infowindow));
		            tvTitle.setText(marker.getTitle());
		            return myContentView;
				}

				@Override
				public View getInfoWindow(Marker arg0) {
					// TODO Auto-generated method stub
					return null;
				}
            	
            });*/
            
            googleMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                	Place place = hmPlace.get(marker);
                	if (place != null) {
                		//Toast.makeText(getBaseContext(), place.place_id, Toast.LENGTH_SHORT).show();
                		Intent intent = new Intent(getApplicationContext(), PlaceDetailsActivity.class);
                		intent.putExtra("placeid", place.place_id);
                		startActivity(intent);
                	}
                }
            });
            
            googleMap.setOnMyLocationChangeListener(new OnMyLocationChangeListener() {

				@Override
				public void onMyLocationChange(Location location) {
					currentLocation = new LatLng( location.getLatitude(), location.getLongitude() );
					
					
				}
            	
            });
            
            googleMap.setOnMyLocationButtonClickListener(new OnMyLocationButtonClickListener() {

				@Override
				public boolean onMyLocationButtonClick() {
					// TODO Auto-generated method stub
					return false;
				}
            	
            });
        	
		}
		
		handleIntent(getIntent());
		
		
		btnSave.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String name;
				if (searchedPlace != null)
					name = searchedPlace.getName();
				else
					name = "";
				DialogFragment dialog = InputDialogFragment.newInstance(name);
                dialog.show(getFragmentManager(), "InputDialogFragment");			
			}
		});
		
		// clear map
		btnClear.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				googleMap.clear();
				if (savedPlace != null) {
					AddMarker(savedPlace.getLocation(), savedPlace.getName(), BitmapDescriptorFactory.HUE_RED);
					btnSave.setEnabled(false);
				}
				else
					btnSave.setEnabled(true);
				searchedPlace = null;
			}
		});
	}
	
	private Marker AddMarker(LatLng location, String title, float color) {
		MarkerOptions marker = new MarkerOptions().position(location).title(title);
		
		marker.icon(BitmapDescriptorFactory.defaultMarker(color));
        Marker m1 = googleMap.addMarker(marker);

        return m1;
	}
	
	@Override
    protected void onNewIntent(Intent intent) {
		setIntent(intent);
        handleIntent(intent);
    }
	
	private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            doSearch(intent.getStringExtra(SearchManager.QUERY));
        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
        	getPlace(intent.getStringExtra(SearchManager.EXTRA_DATA_KEY));
        }
    }
	
	private void doSearch(String query){
        Bundle data = new Bundle();
        data.putString("query", query);
        getSupportLoaderManager().restartLoader(0, data, this);
    }
	
	private void getPlace(String query){
        Bundle data = new Bundle();
        data.putString("query", query);
        getSupportLoaderManager().restartLoader(1, data, this);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.map_plan, menu);
	    
	    // Associate searchable configuration with the SearchView
	    SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	    MenuItem mSearchMenuItem = menu.findItem(R.id.action_search);
	    searchView = (SearchView) MenuItemCompat.getActionView(mSearchMenuItem);
	    
	    if (searchManager != null)
	    {
	    	if (searchView != null)
	    		searchView.setSearchableInfo(searchManager.getSearchableInfo(
	    	            new ComponentName(getApplicationContext(), MapPlanActivity.class)));
	    }
	    return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_layers) {
			DialogFragment dialog = new LayersDialogFragment();
            dialog.show(getFragmentManager(), "LayersDialogFragment");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle query) {
		CursorLoader cLoader = null;
        if(arg0==0)
            cLoader = new CursorLoader(getBaseContext(), GooglePlaces.SEARCH_URI, null, null, new String[]{ query.getString("query") }, null);
        else if(arg0==1)
            cLoader = new CursorLoader(getBaseContext(), GooglePlaces.DETAILS_URI, null, null, new String[]{ query.getString("query") }, null);
        return cLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {
		searchedPlace = null;
		MarkerOptions markerOptions = null;
        LatLng position = null;
        googleMap.clear();
        while(c.moveToNext()){
            position = new LatLng(Double.parseDouble(c.getString(1)),Double.parseDouble(c.getString(2)));
            AddMarker(position, c.getString(0), BitmapDescriptorFactory.HUE_AZURE);
        }
        if(position!=null){
        	btnClear.setEnabled(true);
            CameraUpdate cameraPosition = CameraUpdateFactory.newLatLng(position);
            googleMap.animateCamera(cameraPosition);
            if (c.getCount() > 1)
            	btnSave.setEnabled(false);
            else {
            	btnSave.setEnabled(true);
            	c.moveToFirst();
            	searchedPlace = new TourPlace(c.getString(0), position);
            }
        }
        if (savedPlace != null) {
        	AddMarker(savedPlace.getLocation(), savedPlace.getName(), BitmapDescriptorFactory.HUE_RED);
        	btnSave.setEnabled(false);
        }
        
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDialogPositiveClick(DialogFragment dialog, String value) {	
		if (searchedPlace != null) {
			searchedPlace.setName(value);
			savedPlace = searchedPlace;
		}
		else
			savedPlace = new TourPlace(value, currentLocation);
		finish();
	}

	@Override
	public void onDialogPositiveClick(LayersDialogFragment dialog, String value) {
		// TODO Auto-generated method stub
		type = value;
		new LoadPlaces().execute();
	}
	
	class LoadPlaces extends AsyncTask<String, String, String> {
		
		PlaceList nearPlaces;
		
		@Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MapPlanActivity.this);
            pDialog.setMessage(Html.fromHtml("<b>Search</b><br/>Loading Places..."));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

		@Override
		protected String doInBackground(String... params) {
			
            GooglePlaces googlePlaces = new GooglePlaces();
             
            try {
                String types = type; 
                 
                // Radius in meters - increase this value if you don't find any places
                double radius = 1000; // 1000 meters
                 
                // get nearest places
                if (searchedPlace != null)
                	nearPlaces = googlePlaces.search(searchedPlace.getLocation().latitude,
                        searchedPlace.getLocation().longitude, radius, types);
                else if (savedPlace != null)
                	nearPlaces = googlePlaces.search(savedPlace.getLocation().latitude,
                			savedPlace.getLocation().longitude, radius, types);
                else
                	nearPlaces = googlePlaces.search(currentLocation.latitude,
                			currentLocation.longitude, radius, types);
                 
 
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
		}
		
		protected void onPostExecute(String file_url) {

            pDialog.dismiss();

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
                        	googleMap.clear();
                        	hmPlace.clear();
                        	if (savedPlace != null)
                        		AddMarker(savedPlace.getLocation(), savedPlace.getName(), BitmapDescriptorFactory.HUE_RED);
                        	if (searchedPlace != null)
                        		AddMarker(searchedPlace.getLocation(), searchedPlace.getName(), BitmapDescriptorFactory.HUE_AZURE);
                            // loop through each place
                        	for (Place place : nearPlaces.results) {
                                Double latitude = place.geometry.location.lat; // latitude
                                Double longitude = place.geometry.location.lng; // longitude
                                
                                LatLng placeLocation = new LatLng(latitude, longitude);                                
                                /*MarkerOptions marker = new MarkerOptions().position(placeLocation);
                                marker.title(place.name);
                                marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                googleMap.addMarker(marker);*/
                                Marker m = AddMarker(placeLocation, place.name, BitmapDescriptorFactory.HUE_GREEN);
                                hmPlace.put(m, place);
                            }
                        }
                    }
                    else if(status.equals("ZERO_RESULTS")){
                        // Zero results found
                        alert.showAlertDialog(MapPlanActivity.this, "Near Places",
                                "Sorry no places found. Try to change the types of places",
                                false);
                    }
                    else if(status.equals("UNKNOWN_ERROR"))
                    {
                        alert.showAlertDialog(MapPlanActivity.this, "Places Error",
                                "Sorry unknown error occured.",
                                false);
                    }
                    else if(status.equals("OVER_QUERY_LIMIT"))
                    {
                        alert.showAlertDialog(MapPlanActivity.this, "Places Error",
                                "Sorry query limit to google places is reached",
                                false);
                    }
                    else if(status.equals("REQUEST_DENIED"))
                    {
                        alert.showAlertDialog(MapPlanActivity.this, "Places Error",
                                "Sorry error occured. Request is denied",
                                false);
                    }
                    else if(status.equals("INVALID_REQUEST"))
                    {
                        alert.showAlertDialog(MapPlanActivity.this, "Places Error",
                                "Sorry error occured. Invalid Request",
                                false);
                    }
                    else
                    {
                        alert.showAlertDialog(MapPlanActivity.this, "Places Error",
                                "Sorry error occured.",
                                false);
                    }
                }
            });
 
        }
		
	}

}
