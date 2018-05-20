package com.mango.utils;

import java.util.ArrayList;
import java.util.Map;
import net.sf.json.*;

/**
 * @author 陈贵豪
 * @since 2017-12-10
 * @version 1.0
 */
public class JsonEncodeFormatter {
	
	/**
	 * 标准回复格式1：在data结构体外，包含有额外的字段，2017.9.23测试通过
	 * @param stateCode 状态码
	 * @param addtionalParams 额外字段
	 * @param data 主消息结构体的内容
	 * @return JSON字串
	 */
	public static String parser(int stateCode,Map<String, String> addtionalParams,Map<String, String> data) {
		JSONObject root = new JSONObject();
		root.put("code", String.valueOf(stateCode));
		
		for (String k : addtionalParams.keySet()) {
			root.put(k,addtionalParams.get(k));
		}
		
		JSONObject dataObj = new JSONObject();
		for (String k : data.keySet()) {
			dataObj.put(k,data.get(k));
		}
		
		root.put("data", dataObj);
		
		return root.toString();
	}
	
	/**
	 * 标准回复格式2：在data结构体外，不包含有额外的字段，2017.9.23测试通过
	 * @param stateCode 状态码
	 * @param data 主消息结构体的内容
	 * @return JSON字串
	 */
	public static String parser(int stateCode,Map<String, String> data) {
		JSONObject root = new JSONObject();

		root.put("code", String.valueOf(stateCode));
		
		JSONObject dataObj = new JSONObject();
		for (String k : data.keySet()) {
			dataObj.put(k,data.get(k));
		}
		
		root.put("data", dataObj);
		
		return root.toString();
	}
	
	/**
	 * 标准回复格式3：在data结构体外，不包含有额外的字段，2017.9.23测试通过
	 * @param stateCode 状态码
	 * @param data_array 主消息结构体的内容，以数组形式返回
	 * @return JSON字串
	 */
	public static String parser(int stateCode,ArrayList<Map<String, String>> data_array){
		JSONObject root = new JSONObject();

		root.put("code", String.valueOf(stateCode));
		
		JSONArray data_arr = new JSONArray();
		
		for (Map<String, String> object : data_array) {
			JSONObject dataObj = new JSONObject();
			for (String k : object.keySet()) {
				dataObj.put(k,object.get(k));
			}
			data_arr.add(dataObj);
		}
		
		root.put("data", data_arr);
		
		return root.toString();
	}
	
	/**
	 * 标准回复格式4：在data结构体外，包含有额外的字段，2017.9.23测试通过
	 * @param stateCode 状态码
	 * @param addtionalParams 额外字段
	 * @param data_array 主消息结构体的内容，以数组形式返回
	 * @return JSON字串
	 */
	
	public static String parser(int stateCode,Map<String, String> addtionalParams,ArrayList<Map<String, String>> data_array){
		JSONObject root = new JSONObject();
		root.put("code", String.valueOf(stateCode));
		
		for (String k : addtionalParams.keySet()) {
			root.put(k,addtionalParams.get(k));
		}
		
		JSONArray data_arr = new JSONArray();
		
		for (Map<String, String> object : data_array) {
			JSONObject dataObj = new JSONObject();
			for (String k : object.keySet()) {
				dataObj.put(k,object.get(k));
			}
			data_arr.add(dataObj);
		}
		
		root.put("data", data_arr);
		
		return root.toString();
	}
	
	/**
	 * 默认快捷消息返回1：快捷回复操作成功，2017.9.23测试通过
	 * @param stateCode 状态码
	 * @param addtionalParams 额外字段
	 * @param data_array 主消息结构体的内容，以数组形式返回
	 * @return JSON字串
	 */
	public static String defaultSuccessfulResponse() {
		JSONObject root = new JSONObject();
		root.put("code", String.valueOf(0));
		root.put("msg", "successful!");
		root.put("timestamp", System.currentTimeMillis());
		return root.toString();
	}
	
	/**
	 * 默认快捷消息返回2：快捷回复操作失败，2017.9.23测试通过
	 * @param stateCode 状态码
	 * @param addtionalParams 额外字段
	 * @param data_array 主消息结构体的内容，以数组形式返回
	 * @return JSON字串
	 */
	public static String defaultFailureResponse() {
		JSONObject root = new JSONObject();
		root.put("code", String.valueOf(999));
		root.put("msg", "Fail!");
		root.put("timestamp", System.currentTimeMillis());
		return root.toString();
	}
	
	/**
	 * 通用消息回复格式：自定义消息 2017.9.23测试通过
	 * @param stateCode 状态码
	 * @param addtionalParams 额外字段
	 * @param data_array 主消息结构体的内容，以数组形式返回
	 * @return JSON字串
	 */
	public static String universalResponse(int stateCode,String msg){
		JSONObject root = new JSONObject();
		root.put("code", String.valueOf(stateCode));
		root.put("msg", msg);
		root.put("timestamp", System.currentTimeMillis());
		return root.toString();
	}
}
