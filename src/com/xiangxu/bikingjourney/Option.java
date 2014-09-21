package com.xiangxu.bikingjourney;

public class Option {
	
	private String title;
	private int drawableId;
	private String types;
	private int fragmentId;
	
	public String getTitle() {
		return this.title;
	}
	
	public int getDrawableId() {
		return this.drawableId;
	}
	
	public String getTypes() {
		return this.types;
	}
	
	public int getFragmentId() {
		return this.fragmentId;
	}

	public Option(String title, int drawableId) {
		this.title = title;
		this.drawableId = drawableId;
	}
	
	public Option(String title, int drawableId, String types) {
		this.title = title;
		this.drawableId = drawableId;
		this.types = types;
	}
	
	public Option(String title, int drawableId, int fragmentId) {
		this.title = title;
		this.drawableId = drawableId;
		this.fragmentId = fragmentId;
	}
}
