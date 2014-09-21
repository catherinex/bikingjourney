/*
 * Load information (address, phone, etc.) of place details
 */

package com.xiangxu.bikingjourney;

import java.util.ArrayList;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class PlaceDetailsFragment extends ListFragment {
	
	private ArrayList<Option> optionList;
	private Context context;
	private GooglePlaceDetailsResult placeDetails;
	
	public void setOptionList(ArrayList<Option> optionList) {
		this.optionList = optionList;
	}
	
	public void setContext(Context context) {
		this.context = context;
	}
	
	public void setPlaceDetails(GooglePlaceDetailsResult placeDetails) {
		this.placeDetails = placeDetails;
	}
	
	public static PlaceDetailsFragment newInstance(Context context, ArrayList<Option> optionList, GooglePlaceDetailsResult placeDetails) {
		PlaceDetailsFragment fragment = new PlaceDetailsFragment();
		fragment.setContext(context);
		fragment.setOptionList(optionList);
		fragment.setPlaceDetails(placeDetails);
		return fragment;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	    
	    OptionAdapter customAdapter = new OptionAdapter(getActivity(), R.layout.option_item, optionList);
	    setListAdapter(customAdapter);
	}
	
	@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Option op = (Option)l.getItemAtPosition(position);
        if (op.getDrawableId() == R.drawable.ic_action_place) {
        	Intent intent = new Intent(getActivity(), PlacesMapActivity.class);
        	intent.putExtra("place_details", placeDetails);
        	startActivity(intent);
        } else if (op.getDrawableId() == R.drawable.ic_action_phone) {
        	Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + placeDetails.formatted_phone_number));
            startActivity(callIntent);
        }
    }

}
