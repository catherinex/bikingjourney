package com.xiangxu.bikingjourney;

import java.io.Serializable;
import java.util.List;

import com.google.api.client.util.Key;
import com.xiangxu.bikingjourney.Place.Geometry;
import com.xiangxu.bikingjourney.Place.Location;

public class GooglePlaceDetailsResult implements Serializable {

	@Key
	public String place_id;
	
    @Key
    public String id;
     
    @Key
    public String name;
     
    @Key
    public String reference;
     
    @Key
    public String icon;
     
    @Key
    public String vicinity;
     
    @Key
    public Geometry geometry;
     
    @Key
    public String formatted_address;
     
    @Key
    public String formatted_phone_number;
    
    @Key
    public String description;
    
    @Key
    public double rating;
    
    @Key
    public List<Review> reviews;
    
    public static class Review implements Serializable {
    	
    	@Key
    	public String author_name;
    }
 
    @Override
    public String toString() {
        return name + " - " + id + " - " + reference;
    }
     
    public static class Geometry implements Serializable
    {
        @Key
        public Location location;
    }
     
    public static class Location implements Serializable
    {
        @Key
        public double lat;
         
        @Key
        public double lng;
    }
}
