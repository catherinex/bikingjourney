package com.xiangxu.bikingjourney;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container_notification, new PlaceholderFragment()).commit();
			
			ArrayList<Option> optionList = new ArrayList<Option>();
			Option option = new Option("Near Me", R.drawable.option);
			optionList.add(option);
			
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container_option, new OptionFragment().newInstance(optionList)).commit();
		}
		
		Button btn = (Button)findViewById(R.id.cameraButton);
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
				startActivity(intent);
				/*FileInputStream fis = null;
				try {
					fis = openFileInput("journey.json");
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 InputStreamReader isr = new InputStreamReader(fis);
				 BufferedReader bufferedReader = new BufferedReader(isr);
				 StringBuilder sb = new StringBuilder();
				 String line;
				 try {
					while ((line = bufferedReader.readLine()) != null) {
					     sb.append(line);
					 }
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				 String json = sb.toString();
				 Toast.makeText(getApplicationContext(), json, Toast.LENGTH_SHORT).show();*/
			}
		});
		
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();

	}
	
	private void loadData() {
		FileInputStream fis = null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_new) {
			Intent intent = new Intent(getApplicationContext(), NewJourneyActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
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
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

}
