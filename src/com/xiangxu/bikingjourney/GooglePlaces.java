package com.xiangxu.bikingjourney;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson.JacksonFactory;

public class GooglePlaces extends ContentProvider {

	/** Global instance of the HTTP transport. */
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
 
    // Google API Key
    private static final String API_KEY = "AIzaSyB0YBWFQ_P-jbglhjYdHFQQoALEpMv-akU";
 
    // Google Places serach url's
    private static final String PLACES_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    private static final String PLACES_TEXT_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/search/json?";
    private static final String PLACES_DETAILS_URL = "https://maps.googleapis.com/maps/api/place/details/json?";
    private static final String PLACES_AUTOCOMPLETE_URL = "https://maps.googleapis.com/maps/api/place/autocomplete/json?";
    private static final String PLACES_PHOTOS_URL = "https://maps.googleapis.com/maps/api/place/photo?";
 
    private double _latitude;
    private double _longitude;
    private double _radius; 
    
    public static final String AUTHORITY = "com.xiangxu.bikingjourney.GooglePlaces";
    public static final Uri SEARCH_URI = Uri.parse("content://"+AUTHORITY+"/search");
    public static final Uri DETAILS_URI = Uri.parse("content://"+AUTHORITY+"/details");
    private static final int SEARCH = 1;
    private static final int SUGGESTIONS = 2;
    private static final int DETAILS = 3;
    // Defines a set of uris allowed with this content provider
    private static final UriMatcher mUriMatcher = buildUriMatcher();
    
    private static UriMatcher buildUriMatcher() {
    	 
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
 
        // URI for "Go" button
        uriMatcher.addURI(AUTHORITY, "search", SEARCH );
 
        // URI for suggestions in Search Dialog
        uriMatcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY,SUGGESTIONS);
 
        // URI for Details
        uriMatcher.addURI(AUTHORITY, "details",DETAILS);
 
        return uriMatcher;
    }
 
    /**
     * Searching places
     * @param latitude - latitude of place
     * @params longitude - longitude of place
     * @param radius - radius of searchable area
     * @param types - type of place to search
     * @return list of places
     * */
    public PlaceList search(double latitude, double longitude, double radius, String types)
            throws Exception {
 
        this._latitude = latitude;
        this._longitude = longitude;
        this._radius = radius;
 
        try {
 
            HttpRequestFactory httpRequestFactory = createRequestFactory(HTTP_TRANSPORT);
            HttpRequest request = httpRequestFactory
                    .buildGetRequest(new GenericUrl(PLACES_SEARCH_URL));
            request.getUrl().put("key", API_KEY);
            request.getUrl().put("location", _latitude + "," + _longitude);
            request.getUrl().put("radius", _radius); // in meters
            request.getUrl().put("sensor", "false");
            if(types != null)
                request.getUrl().put("types", types);
            Log.i("lat", Double.toString(_latitude));
 
            PlaceList list = request.execute().parseAs(PlaceList.class);
            Log.i("lng", Double.toString(_longitude));
            // Check log cat for places response status
            Log.d("Places Status", "" + list.status);
            return list;
 
        } catch (HttpResponseException e) {
            Log.e("Error:", e.getMessage());
            return null;
        }
 
    }
 
    /**
     * Searching single place full details
     * @param refrence - reference id of place
     *                 - which you will get in search api request
     * */
    public PlaceDetails getPlaceDetails(String reference) throws Exception {
        try {
 
            HttpRequestFactory httpRequestFactory = createRequestFactory(HTTP_TRANSPORT);
            HttpRequest request = httpRequestFactory
                    .buildGetRequest(new GenericUrl(PLACES_DETAILS_URL));
            request.getUrl().put("key", API_KEY);
            request.getUrl().put("reference", reference);
            request.getUrl().put("sensor", "false");
 
            PlaceDetails place = request.execute().parseAs(PlaceDetails.class);
             
            return place;
 
        } catch (HttpResponseException e) {
            throw e;
        }
    }
    
    public GooglePlaceDetails getGooglePlaceDetails(String place_id) throws Exception {
        try {
 
            HttpRequestFactory httpRequestFactory = createRequestFactory(HTTP_TRANSPORT);
            HttpRequest request = httpRequestFactory
                    .buildGetRequest(new GenericUrl(PLACES_DETAILS_URL));
            request.getUrl().put("key", API_KEY);
            request.getUrl().put("placeid", place_id);
 
            GooglePlaceDetails place = request.execute().parseAs(GooglePlaceDetails.class);
             
            return place;
 
        } catch (HttpResponseException e) {
            throw e;
        }
    }
    
    public PlaceAutocomplete searchAutocomplete(String qry) throws IOException {
    	HttpRequestFactory httpRequestFactory = createRequestFactory(HTTP_TRANSPORT);
    	HttpRequest request = httpRequestFactory
                .buildGetRequest(new GenericUrl(PLACES_AUTOCOMPLETE_URL));
    	request.getUrl().put("key", API_KEY);
    	request.getUrl().put("input", URLEncoder.encode(qry, "utf-8"));
    	request.getUrl().put("types", "geocode");
    	PlaceAutocomplete result = request.execute().parseAs(PlaceAutocomplete.class);
    	return result;
    }
    
    public Bitmap getPlacePhoto(String photo_reference) throws IOException {
    	HttpRequestFactory httpRequestFactory = createRequestFactory(HTTP_TRANSPORT);
    	HttpRequest request = httpRequestFactory
                .buildGetRequest(new GenericUrl(PLACES_PHOTOS_URL));
    	request.getUrl().put("key", API_KEY);
    	request.getUrl().put("photoreference", photo_reference);
    	request.getUrl().put("maxwidth", 200);
    	Bitmap result = BitmapFactory.decodeStream(request.execute().getContent());
    	return result;
    }
 
    /**
     * Creating http request Factory
     * */
    public static HttpRequestFactory createRequestFactory(
            final HttpTransport transport) {
        return transport.createRequestFactory(new HttpRequestInitializer() {
            public void initialize(HttpRequest request) {
                HttpHeaders headers = new HttpHeaders();
                headers.setUserAgent("AndroidHive-Places-Test");
                request.setHeaders(headers);
                JsonObjectParser parser = new JsonObjectParser(new JacksonFactory());
                request.setParser(parser);
            }
        });
    }

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri arg0, ContentValues arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		Cursor c = null;
		MatrixCursor mCursor = null;
		switch(mUriMatcher.match(uri)){
		case SEARCH:
            // Defining a cursor object with columns description, lat and lng
            mCursor = new MatrixCursor(new String[] { "description","lat","lng" });

            try {
            	List<GooglePlaceAutocompletePrediction> list = searchAutocomplete(selectionArgs[0]).predictions;
 
                // Finding latitude and longitude for each places using Google Places Details API
                for(int i=0;i<list.size();i++){
 
                    // Get Place details
                	Place place = getPlaceDetails(list.get(i).reference).result;
 
                    // Adding place details to cursor
                    mCursor.addRow(new String[]{ place.formatted_address , Double.toString(place.geometry.location.lat) , Double.toString(place.geometry.location.lng) });
 
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
				e.printStackTrace();
			}
            break;
            
			case SUGGESTIONS :
				// Defining a cursor object with columns id, SUGGEST_COLUMN_TEXT_1, SUGGEST_COLUMN_INTENT_EXTRA_DATA
				mCursor = new MatrixCursor(new String[] { "_id", SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA } );
				try {
					List<GooglePlaceAutocompletePrediction> list = searchAutocomplete(selectionArgs[0]).predictions;
					// Creating cursor object with places
		            for(int i=0;i<list.size();i++){
		                //HashMap<String, String> hMap = (HashMap<String, String>) list.get(i);

		                // Adding place details to cursor
		                mCursor.addRow(new String[] { Integer.toString(i), list.get(i).description, list.get(i).reference });
		            }
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				
				break;
				
			case DETAILS :
	            // Defining a cursor object with columns description, lat and lng
	            mCursor = new MatrixCursor(new String[] { "description","lat","lng" });
	
	            Place place;
	            try {
	            	place = getPlaceDetails(selectionArgs[0]).result;
	            	mCursor.addRow(new String[]{ place.formatted_address 
	            			, Double.toString(place.geometry.location.lat) 
	            			, Double.toString(place.geometry.location.lng)
	            			});
	            } catch (Exception e) {
	            	e.printStackTrace();
	            }	                
	            break;
		}
		
		c = mCursor;
		return c;
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return 0;
	}


}
