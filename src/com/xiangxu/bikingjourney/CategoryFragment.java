package com.xiangxu.bikingjourney;

import java.util.ArrayList;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class CategoryFragment extends ListFragment {
	
	private ArrayList<Option> optionList;
	private Context context;
	private boolean isNearbyMe = false;
	
	public void setOptionList(ArrayList<Option> optionList) {
		this.optionList = optionList;
	}
	
	public void setContext(Context context) {
		this.context = context;
	}
	
	public void setIsNearbyMe(boolean isNearbyMe) {
		this.isNearbyMe = isNearbyMe;
	}
	
	public static CategoryFragment newInstance(Context context, ArrayList<Option> optionList) {
		CategoryFragment fragment = new CategoryFragment();
		fragment.setContext(context);
		fragment.setOptionList(optionList);
		return fragment;
	}
	
	public static CategoryFragment newInstance(Context context, ArrayList<Option> optionList, boolean isNearbyMe) {
		CategoryFragment fragment = new CategoryFragment();
		fragment.setContext(context);
		fragment.setOptionList(optionList);
		fragment.setIsNearbyMe(isNearbyMe);
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
        //Log.i(TAG, "[onListItemClick] Selected Position "+ position);
        Option op = (Option)l.getItemAtPosition(position);

        if (!isNearbyMe) {
        	Intent intent = new Intent(getActivity(), NearbyActivity.class);
        	startActivity(intent);
        } else {
        	Intent intent = new Intent(getActivity(), PlaceListActivity.class);
        	intent.putExtra("types", op.getTypes());
        	startActivity(intent);
        }
    }

}
