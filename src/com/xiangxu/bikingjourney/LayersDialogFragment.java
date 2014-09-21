package com.xiangxu.bikingjourney;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class LayersDialogFragment extends DialogFragment {

	public interface LayersDialogListener {
        public void onDialogPositiveClick(LayersDialogFragment dialog, String value);
    }
	
	LayersDialogListener mListener;
	View dialogView;
	RadioGroup rgLayers;
	String type;
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the ConfirmationDialogListener so we can send events to the host
            mListener = (LayersDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Display on Map");
        LayoutInflater inflater = getActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.fragment_layers_dialog, null);
        builder.setView(dialogView);
        rgLayers = (RadioGroup) dialogView.findViewById(R.id.rg_layers);
        rgLayers.check(R.id.rb_hotel);
        rgLayers.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				//RadioButton radioButton = (RadioButton) dialogView.findViewById(checkedId);
				switch (checkedId) {
					case R.id.rb_bar:
						type = "bar";
						break;
					case R.id.rb_cafe:
						type = "cafe";
						break;
					case R.id.rb_food:
						type = "food";
						break;
					case R.id.rb_hotel:
						type = "lodging";
						break;
					case R.id.rb_restaurant:
						type = "restaurant";
						break;
					case R.id.rb_shop:
						type = "store";
						break;
				}
			}
        	
        });
        
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                           mListener.onDialogPositiveClick(LayersDialogFragment.this, type);
                   }
               })
               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   LayersDialogFragment.this.getDialog().cancel();
                   }
               });
        
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
