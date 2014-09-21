package com.xiangxu.bikingjourney;

import java.io.Serializable;
import java.util.List;

import com.google.api.client.util.Key;

public class PlaceList implements Serializable {

	@Key
    public String status;
 
    @Key
    public List<Place> results;
    
}
