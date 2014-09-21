package com.xiangxu.bikingjourney;

import java.util.List;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class OptionAdapter extends ArrayAdapter<Option>{

	private List<Option> items;
	private int layoutResourceId;
	private Context context;
	
	public OptionAdapter(Context context, int layoutResourceId, List<Option> items) {
		super(context, layoutResourceId, items);
		this.layoutResourceId = layoutResourceId;
		this.items = items;
		this.context = context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View row = convertView;
		OptionHolder holder = null;
		
		LayoutInflater inflater = LayoutInflater.from(getContext());
		row = inflater.inflate(layoutResourceId, parent, false);
		
		holder = new OptionHolder();
		holder.option = items.get(position);
		holder.tv = (TextView)row.findViewById(R.id.tv_title);
		holder.icon = (ImageView)row.findViewById(R.id.icon_option);
		
		if (holder.option.getFragmentId() == R.id.container_information)
			holder.tv.setTextSize(15);
		if (holder.option.getDrawableId() == R.drawable.ic_action_directions
			|| holder.option.getDrawableId() == R.drawable.ic_action_web_site)
			holder.tv.setTextColor(context.getResources().getColor(R.color.actionbarcolor));
		
		
		row.setTag(holder);
		setupItem(holder);
		return row;
	}
	
	private ColorStateList getResource(int actionbarcolor) {
		// TODO Auto-generated method stub
		return null;
	}

	private void setupItem(final OptionHolder holder) {
		
		holder.tv.setText(holder.option.getTitle());
		
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), holder.option.getDrawableId());
		holder.icon.setImageBitmap(bitmap);
	}
	
	public static class OptionHolder {
		Option option;
		ImageView icon;
		TextView tv;
	}
}
