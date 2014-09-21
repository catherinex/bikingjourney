package com.xiangxu.bikingjourney;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class OptionFragment extends ListFragment {
	
	private ArrayList<Option> optionList;

	public void setOptionList(ArrayList<Option> optionList) {
		this.optionList = optionList;
	}

	public static OptionFragment newInstance(ArrayList<Option> optionList) {
		OptionFragment fragment = new OptionFragment();
		fragment.setOptionList(optionList);
		return fragment;
	}
	
	@Override
	  public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	    
	    OptionAdapter customAdapter = new OptionAdapter(getActivity(), R.layout.option_item, optionList);
	    setListAdapter(customAdapter);
	    
	    
	    /*ProductListAdapter customAdapter = new ProductListAdapter(getActivity(), R.layout.list_view_item, ProductList.productList);
	    setListAdapter(customAdapter);*/
	  }
	
	@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        //Log.i(TAG, "[onListItemClick] Selected Position "+ position);
        /*Option op = (Option)l.getItemAtPosition(position);
        Toast.makeText(getActivity(), op.getTitle(), Toast.LENGTH_SHORT).show();*/
        
        //Intent intent = new Intent(getActivity(), ShowPlaceActivity.class);
        Intent intent = new Intent(getActivity(), CategoryActivity.class);
        intent.putExtra("isNearbyMe", true);
		startActivity(intent);
    }

}
