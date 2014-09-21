package com.xiangxu.bikingjourney;

public class Tour {
	
	private String tourId;
	private String journeyId;
	private TourPlace from;
	private TourPlace to;
	
	public String getTourId() {
		return tourId;
	}
	
	public TourPlace getFrom() {
		return from;
	}
	
	public void setFrom (TourPlace from) {
		this.from = from;
	}
	
	public TourPlace getTo() {
		return to;
	}
	
	public void setTo (TourPlace to) {
		this.to = to;
	}
	
	public Tour (String tourId) {
		this.tourId = tourId;
	}
	
	public Tour (TourPlace from, TourPlace to) {
		this.from = from;
		this.to = to;
	}

}
