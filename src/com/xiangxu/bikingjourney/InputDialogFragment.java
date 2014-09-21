package com.xiangxu.bikingjourney;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class InputDialogFragment extends DialogFragment {
	
	private String name;

	public interface InputDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, String value);
    }
	
	public InputDialogFragment() {}
	
	public static InputDialogFragment newInstance (String name) {
		InputDialogFragment fragment = new InputDialogFragment();
		fragment.name = name;
		return fragment;
	}
	
	InputDialogListener mListener;
	View dialogView;
	EditText valueView;
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the ConfirmationDialogListener so we can send events to the host
            mListener = (InputDialogListener) activity;
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
        builder.setTitle("Input Location Name");
        LayoutInflater inflater = getActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.fragment_input_dialog, null);
        builder.setView(dialogView);
        valueView = (EditText) dialogView.findViewById(R.id.et_location_name);
        valueView.setText(name);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   //EditText valueView = (EditText) dialogView.findViewById(R.id.et_location_name);
                       if(valueView != null) 
                       {
                           String value = valueView.getText().toString();
                           mListener.onDialogPositiveClick(InputDialogFragment.this, value);
                       }
                   }
               })
               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   InputDialogFragment.this.getDialog().cancel();
                   }
               });
        
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
