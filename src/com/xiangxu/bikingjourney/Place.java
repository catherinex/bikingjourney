package com.xiangxu.bikingjourney;

import java.io.Serializable;
import java.util.List;

import com.google.api.client.util.Key;

public class Place implements Serializable {
	
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
    
    @Key
    public List<Photo> photos;
    
    public static class Photo implements Serializable {
    	
    	@Key
    	public String photo_reference;
    }
    
    @Key
    public double rating;
    
    @Key
    public OpeningHours opening_hours;
    
    public static class OpeningHours implements Serializable {
    	
    	@Key
    	public boolean open_now;
    }
    
    @Key
    public int price_level;
}
