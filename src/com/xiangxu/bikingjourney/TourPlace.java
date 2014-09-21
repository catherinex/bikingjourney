package com.xiangxu.bikingjourney;

import com.google.android.gms.maps.model.LatLng;

public class TourPlace {
	
	private String name;
	private LatLng location;
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public LatLng getLocation() {
		return this.location;
	}
	
	public TourPlace (String name, LatLng location) {
		this.name = name;
		this.location = location;
	}

}
