package com.xiangxu.bikingjourney;

import java.io.Serializable;

import com.google.api.client.util.Key;

public class GooglePlaceAutocompletePrediction implements Serializable {
	
	@Key
    public String description;
	
	@Key
	public String place_id;
	
	@Key
    public String reference;
	
    @Key
    public String id;
    
    @Override
    public String toString() {
        return description;
    }

}
