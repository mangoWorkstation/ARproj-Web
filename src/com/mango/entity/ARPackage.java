package com.mango.entity;

import java.lang.reflect.Field;
import java.util.HashMap;

public class ARPackage implements UniversalEntity{
	private String uid;
	private String roomid;
	private float latitude;
	private float longitude;
	private String content;
	public String getUid() {
		return uid;
	}
	public String getRoomid() {
		return roomid;
	}
	public float getLatitude() {
		return latitude;
	}
	public float getLongitude() {
		return longitude;
	}
	public String getContent() {
		return content;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public void setRoomid(String roomid) {
		this.roomid = roomid;
	}
	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}
	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String str = "["+this.getClass().getName()+"] ";
		Field[] fields = this.getClass().getDeclaredFields();
		for(Field f:fields) {
			try {
				str += f.getName() + " = " + String.valueOf(f.get(this))+";";
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return str;
	}
	@Override
	public HashMap<String, String> toHashMap() {
		// TODO Auto-generated method stub
		HashMap<String, String> eHashMap = new HashMap<>();
		Field[] fields = this.getClass().getDeclaredFields();
		for(Field f:fields) {
			try {
				eHashMap.put(f.getName(),String.valueOf(f.get(this)));
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return eHashMap;
	}
	
	

}
