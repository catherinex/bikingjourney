package com.xiangxu.bikingjourney;

import java.util.ArrayList;
import java.util.List;

import com.xiangxu.bikingjourney.GooglePlaceDetailsResult.Review;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.ProgressDialog;
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
import android.widget.RatingBar;
import android.widget.TextView;
import android.os.Build;

public class PlaceDetailsActivity extends ActionBarActivity {
	
	private String place_id;
	
	private ProgressDialog pDialog;
	
	private GooglePlaceDetails placeDetails;
	
	private AlertDialogManager alert;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_place_details);
		
		place_id = getIntent().getStringExtra("placeid");
		int visibility = getIntent().getIntExtra("btnSavePlace", View.VISIBLE);

		RatingBar rbPlace = (RatingBar)findViewById(R.id.rb_place);
		Button btnSavePlace = (Button)findViewById(R.id.btn_save_place);
		
		rbPlace.setEnabled(false);
		btnSavePlace.setVisibility(visibility);
		
		new LoadSinglePlaceDetails().execute();
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
     * Background Async Task to Load Google places
     * */
    class LoadSinglePlaceDetails extends AsyncTask<String, String, String> {
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(PlaceDetailsActivity.this);
            pDialog.setMessage("Loading profile ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        /**
         * getting Profile JSON
         * */
        protected String doInBackground(String... args) {
             
            // creating Places class object
            GooglePlaces googlePlaces = new GooglePlaces();
 
            // Check if used is connected to Internet
            try {
                placeDetails = googlePlaces.getGooglePlaceDetails(place_id);
 
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
 
        /**
         * After completing background task Dismiss the progress dialog
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
                    if(placeDetails != null){
                        String status = placeDetails.status;
                         
                        // check place deatils status
                        // Check for all possible status
                        if(status.equals("OK")){
                            if (placeDetails.result != null) {
                                String name = placeDetails.result.name;
                                String address = placeDetails.result.formatted_address;
                                String phone = placeDetails.result.formatted_phone_number;
                                String latitude = Double.toString(placeDetails.result.geometry.location.lat);
                                String longitude = Double.toString(placeDetails.result.geometry.location.lng);
                                double rating = placeDetails.result.rating;
                                List<Review> reviews = placeDetails.result.reviews;
                                int numOfReviews = 0;
                                
                                setTitle(name);
                                 
                                // Displaying all the details in the view
                                TextView lbl_name = (TextView) findViewById(R.id.tv_name_place);
                                RatingBar lbl_rating = (RatingBar) findViewById(R.id.rb_place);
                                TextView lbl_reviews = (TextView) findViewById(R.id.tv_reviews_place);
                                 
                                // Check for null data from google
                                // Sometimes place details might missing
                                name = name == null ? "Not present" : name; // if name is null display as "Not present"
                                address = address == null ? "Not present" : address;
                                phone = phone == null ? "Not present" : phone;
                                latitude = latitude == null ? "Not present" : latitude;
                                longitude = longitude == null ? "Not present" : longitude;
                                numOfReviews = reviews == null ? numOfReviews : reviews.size();
                                 
                                lbl_name.setText(name);
                                lbl_rating.setRating((float) rating);
                                lbl_reviews.setText(numOfReviews + " Reviews");
                                
                                // load information list fragment                              
                                ArrayList<Option> options = new ArrayList<Option>();
                                Option infoAddress = new Option(address, R.drawable.ic_action_place, R.id.container_information);
                                options.add(infoAddress);
                                Option infoTelephone = new Option(phone, R.drawable.ic_action_phone, R.id.container_information);
                                options.add(infoTelephone);
                                Option infoDirections = new Option("Directions", R.drawable.ic_action_directions, R.id.container_information);
                                options.add(infoDirections);
                                Option infoWebsite = new Option("Visit Website", R.drawable.ic_action_web_site, R.id.container_information);
                                options.add(infoWebsite);
                    			
                    			getFragmentManager().beginTransaction()
                    					.add(R.id.container_information, new PlaceDetailsFragment().newInstance(getApplicationContext(), options, placeDetails.result)).commit();
                            }
                        }
                        else if(status.equals("ZERO_RESULTS")){
                            alert.showAlertDialog(PlaceDetailsActivity.this, "Near Places",
                                    "Sorry no place found.",
                                    false);
                        }
                        else if(status.equals("UNKNOWN_ERROR"))
                        {
                            alert.showAlertDialog(PlaceDetailsActivity.this, "Places Error",
                                    "Sorry unknown error occured.",
                                    false);
                        }
                        else if(status.equals("OVER_QUERY_LIMIT"))
                        {
                            alert.showAlertDialog(PlaceDetailsActivity.this, "Places Error",
                                    "Sorry query limit to google places is reached",
                                    false);
                        }
                        else if(status.equals("REQUEST_DENIED"))
                        {
                            alert.showAlertDialog(PlaceDetailsActivity.this, "Places Error",
                                    "Sorry error occured. Request is denied",
                                    false);
                        }
                        else if(status.equals("INVALID_REQUEST"))
                        {
                            alert.showAlertDialog(PlaceDetailsActivity.this, "Places Error",
                                    "Sorry error occured. Invalid Request",
                                    false);
                        }
                        else
                        {
                            alert.showAlertDialog(PlaceDetailsActivity.this, "Places Error",
                                    "Sorry error occured.",
                                    false);
                        }
                    }else{
                        alert.showAlertDialog(PlaceDetailsActivity.this, "Places Error",
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
			View rootView = inflater.inflate(R.layout.fragment_place_details,
					container, false);
			return rootView;
		}
	}

}
