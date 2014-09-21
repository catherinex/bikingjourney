package com.xiangxu.bikingjourney;

import java.util.HashMap;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;
import com.xiangxu.bikingjourney.OptionAdapter.OptionHolder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PlaceListAdapter extends ArrayAdapter<Place> {
	
	private List<Place> items;
	private int layoutResourceId;
	private Context context;
	PlaceHolder holder = null;
	private HashMap<String, Bitmap> photos;
	private LatLng currentPosition;

	public PlaceListAdapter(Context context, int layoutResourceId, List<Place> items
			, HashMap<String, Bitmap> photos
			, LatLng currentPosition) {
		super(context, layoutResourceId, items);
		this.layoutResourceId = layoutResourceId;
		this.items = items;
		this.context = context;
		this.photos = photos;
		this.currentPosition = currentPosition;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View row = convertView;
		
		
		LayoutInflater inflater = LayoutInflater.from(getContext());
		row = inflater.inflate(layoutResourceId, parent, false);
		
		holder = new PlaceHolder();
		holder.place = items.get(position);
		holder.icon = (ImageView)row.findViewById(R.id.iv_photo);
		holder.name = (TextView)row.findViewById(R.id.tv_name_place);
		holder.distance = (TextView)row.findViewById(R.id.tv_distance);
		holder.rating = (TextView)row.findViewById(R.id.tv_rating);
		holder.openingHours = (TextView)row.findViewById(R.id.tv_openinghours);
		
		row.setTag(holder);
		setupItem(holder);
		return row;
	}
	
	private void setupItem(final PlaceHolder holder) {
		
		Bitmap photo = photos.get(holder.place.place_id);
		if (photo != null)
			holder.icon.setImageBitmap(photo);
			
		
		holder.name.setText(holder.place.name);
		
		float[] distance = new float[1];
		Location.distanceBetween(currentPosition.latitude, currentPosition.longitude, holder.place.geometry.location.lat, holder.place.geometry.location.lng, distance);
		holder.distance.setText(String.format("%.0f", distance[0]) + " m");
		holder.rating.setText("Rating: " + holder.place.rating);
		if (holder.place.opening_hours != null) {
			if (holder.place.opening_hours.open_now)
				holder.openingHours.setText("Open now");
			else
				holder.openingHours.setText("Close now");
		} else
			holder.openingHours.setText("");
	}
	
	public static class PlaceHolder {
		Place place;
		ImageView icon;
		TextView name;
		TextView distance;
		TextView rating;
		TextView openingHours;
	}
}
