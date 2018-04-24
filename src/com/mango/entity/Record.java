package com.mango.entity;

import java.lang.reflect.Field;
import java.util.HashMap;

public class Record implements UniversalEntity{
	private String uid;
	private String userid;
	private String start_t;
	private String end_t;
	private int stepCount;
	private int arCount;
	
	public String getUid() {
		return uid;
	}
	public String getUserid() {
		return userid;
	}
	public String getStart_t() {
		return start_t;
	}
	public String getEnd_t() {
		return end_t;
	}
	public int getStepCount() {
		return stepCount;
	}
	public int getArCount() {
		return arCount;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public void setStart_t(String start_t) {
		this.start_t = start_t;
	}
	public void setEnd_t(String end_t) {
		this.end_t = end_t;
	}
	
	public void setStepCount(int stepCount) {
		this.stepCount = stepCount;
	}
	public void setArCount(int arCount) {
		this.arCount = arCount;
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
