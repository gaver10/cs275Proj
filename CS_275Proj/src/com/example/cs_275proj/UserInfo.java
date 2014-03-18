package com.example.cs_275proj;

public class UserInfo {
	private String userId;
	private String lat;
	private String lon;
	private String name;

	public void setUserId(String id){
		userId = id;
	}
	
	public void setLat(String l){
		lat = l;
	}
	
	public void setLon(String l){
		lon = l;
	}
	
	public String getLon(){
		return lon;
	}
	
	public String getLat(){
		return lat;
	}
	
	public String getUserId(){
		return userId;
	}
	
	public void setName(String n){
		name = n;
	}
	
	public String getName(){
		return name;
	}
}

