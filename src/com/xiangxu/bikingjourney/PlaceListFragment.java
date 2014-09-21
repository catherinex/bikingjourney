package com.xiangxu.bikingjourney;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class PlaceListFragment extends ListFragment {
	
	private List<Place> placeList;
	private HashMap<String, Bitmap> photos;
	private LatLng currentPosition;
	private Context context;
	
	public void setPlaceList(List<Place> placeList) {
		this.placeList = placeList;
	}
	
	public void setPhotos(HashMap<String, Bitmap> photos) {
		this.photos = photos;
	}
	
	public void setCurrentPosition(LatLng currentPosition) {
		this.currentPosition = currentPosition;
	}
	
	public void setContext(Context context) {
		this.context = context;
	}


	public static PlaceListFragment newInstance(Context context, List<Place> placeList
			, HashMap<String, Bitmap> photos
			, LatLng currentPosition) {
		PlaceListFragment fragment = new PlaceListFragment();
		fragment.setPlaceList(placeList);
		fragment.setPhotos(photos);
		fragment.setCurrentPosition(currentPosition);
		fragment.setContext(context);
		return fragment;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	    
	    PlaceListAdapter customAdapter = new PlaceListAdapter(getActivity(), R.layout.place_list_item
	    		, placeList, photos, currentPosition);
	    setListAdapter(customAdapter);
	}
	
	@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        //Log.i(TAG, "[onListItemClick] Selected Position "+ position);
        Place place = (Place)l.getItemAtPosition(position);
        Intent intent = new Intent(context, PlaceDetailsActivity.class);
		intent.putExtra("placeid", place.place_id);
		intent.putExtra("btnSavePlace", View.INVISIBLE);
		startActivity(intent);

    }
}
