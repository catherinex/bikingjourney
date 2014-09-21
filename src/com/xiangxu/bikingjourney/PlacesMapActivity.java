package com.xiangxu.bikingjourney;

import java.util.HashMap;
import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.xiangxu.bikingjourney.GooglePlaceDetailsResult.Review;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.app.ActionBar;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PlacesMapActivity extends ActionBarActivity
implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {
	
	LocationRequest locationRequest;
	LocationClient locationClient;
	Location myLocation;
	
	private GoogleMap googleMap;
	AlertDialogManager alert = new AlertDialogManager();
	
	// Nearest places
    PlaceList nearPlaces;
    GooglePlaceDetailsResult placeDetails;
    HashMap<Marker, Place> hmPlace = new HashMap<Marker, Place>();
     
    double latitude;
    double longitude;
    
    private LatLng userLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_places_map);
		
		locationClient = new LocationClient(this, this, this);
		locationRequest = new LocationRequest();
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		
		MapFragment fm = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
		googleMap = fm.getMap();
		googleMap.setMyLocationEnabled(true);
		
		MarkerOptions marker;
		LatLng placeLocation;
		
		// Getting intent data
        Intent i = getIntent();
        if (i.getSerializableExtra("near_places") != null) {
        	// Nearplaces list
            nearPlaces = (PlaceList) i.getSerializableExtra("near_places");

        // check for null in case it is null
        if (nearPlaces.results != null) {
            // loop through all the places
            for (Place place : nearPlaces.results) {
                latitude = place.geometry.location.lat; // latitude
                longitude = place.geometry.location.lng; // longitude
                
                placeLocation = new LatLng(latitude, longitude);                                
                marker = new MarkerOptions().position(placeLocation);
                marker.title(place.name);
                marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                Marker m = googleMap.addMarker(marker);
                hmPlace.put(m, place);
            }
        }
        } else if (i.getSerializableExtra("place_details") != null) {
        	
        	placeDetails = (GooglePlaceDetailsResult)i.getSerializableExtra("place_details");
        	
        	googleMap.setInfoWindowAdapter(new InfoWindowAdapter() {

    			@Override
    			public View getInfoContents(Marker marker) {
    				View myContentView = getLayoutInflater().inflate(
    	                    R.layout.info_window, null);
    				myContentView.setLayoutParams(new RelativeLayout.LayoutParams(400, 150));
    	            TextView tvTitle = ((TextView) myContentView
    	                    .findViewById(R.id.tv_name_infowindow));
    	            RatingBar rbRating = (RatingBar)myContentView.findViewById(R.id.rb_rating);
    	            TextView tvReviews = (TextView)myContentView.findViewById(R.id.tv_review);
    	            
    	            tvTitle.setText(marker.getTitle());
    	            
    	            rbRating.setEnabled(false);
    	            double rating = placeDetails.rating;
    	            rbRating.setRating((float) rating);
    	            
    	            List<Review> reviews = placeDetails.reviews;
                    int numOfReviews = 0;
                    numOfReviews = reviews == null ? numOfReviews : reviews.size();
                    tvReviews.setText(numOfReviews + " Reviews");
    	            return myContentView;
    			}

    			@Override
    			public View getInfoWindow(Marker arg0) {
    				return null;
    			}
        	});
    			
        	
        	latitude = placeDetails.geometry.location.lat;
        	longitude = placeDetails.geometry.location.lng; 
            
            placeLocation = new LatLng(latitude, longitude);                                
            marker = new MarkerOptions().position(placeLocation);
            marker.title(placeDetails.name);
            marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            googleMap.addMarker(marker).showInfoWindow();
            
            ActionBar actionBar = getActionBar();
            actionBar.setTitle(placeDetails.name);
            actionBar.setSubtitle(placeDetails.formatted_address);
            
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(placeLocation,13));
        	
        
        }
        
        googleMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
            	String place_id = null;
            	if (hmPlace.size() > 0) {
            	Place place = hmPlace.get(marker);
            	if (place != null) {
            		place_id = place.place_id;
            		
            	}
            	} else if (placeDetails != null) {
            		place_id = placeDetails.place_id;
            	}
            	Intent intent = new Intent(getApplicationContext(), PlaceDetailsActivity.class);
        		intent.putExtra("placeid", place_id);
        		intent.putExtra("btnSavePlace", View.INVISIBLE);
        		startActivity(intent);
            }
        });
        
	}
	
	@Override
    protected void onStart() {
        super.onStart();
        locationClient.connect();
    }
	
	@Override
    protected void onStop() {
		locationClient.disconnect();
        super.onStop();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
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

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_places_map,
					container, false);
			return rootView;
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		locationClient.removeLocationUpdates(this);
		myLocation = location;
		userLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
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
		userLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
		/*MarkerOptions marker = new MarkerOptions().position(userLocation).title("You are here");
        googleMap.addMarker(marker);*/
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,13));
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}

}
