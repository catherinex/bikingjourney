package com.xiangxu.bikingjourney;

import java.io.Serializable;

import com.google.api.client.util.Key;

public class GooglePlaceDetails implements Serializable {

	@Key
    public String status;
     
    @Key
    public GooglePlaceDetailsResult result;
 
    @Override
    public String toString() {
        if (result!=null) {
            return result.toString();
        }
        return super.toString();
    }
}
