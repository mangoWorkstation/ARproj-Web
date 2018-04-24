package com.mango.entity;

import java.lang.reflect.Field;
import java.util.HashMap;

public class TeamPool implements UniversalEntity{
	private String uid;
	private String roomid;
	private String userid;
	private float percent;
	private String done_t;
	private int stepCount;
	private int ARCount;
	private String start_t;
	
	public String getUid() {
		return uid;
	}
	public String getRoomid() {
		return roomid;
	}
	public String getUserid() {
		return userid;
	}
	public float getPercent() {
		return percent;
	}
	public String getDone_t() {
		return done_t;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public void setRoomid(String roomid) {
		this.roomid = roomid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public void setPercent(float percent) {
		this.percent = percent;
	}
	public void setDone_t(String done_t) {
		this.done_t = done_t;
	}
	
	public int getStepCount() {
		return stepCount;
	}
	public int getArCount() {
		return ARCount;
	}
	public void setStepCount(int stepCount) {
		this.stepCount = stepCount;
	}
	public void setArCount(int arCount) {
		this.ARCount = arCount;
	}
	public String getStart_t() {
		return start_t;
	}
	public void setStart_t(String start_t) {
		this.start_t = start_t;
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
