package com.xiangxu.bikingjourney;

import java.util.ArrayList;

public class GlobalData {

	public static ArrayList<Journey> journeyList = new ArrayList<Journey>();
	public static ArrayList<Tour> tourList = new ArrayList<Tour>();
	
	public static ArrayList<Option> getLogisticCategoryList() {
		ArrayList<Option> result = new ArrayList<Option>();
		Option hotels = new Option("Hotels", R.drawable.bed_icon, "lodging");
		result.add(hotels);
		Option restaurants = new Option("Restaurants", R.drawable.restaurant_icon, "restaurant");
		result.add(restaurants);
		Option cafes = new Option("Cafes", R.drawable.coffee_icon, "cafe|bar");
		result.add(cafes);
		Option shops = new Option("Shops", R.drawable.cart_icon, "store");
		result.add(shops);
		return result;
	}
	
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
}
