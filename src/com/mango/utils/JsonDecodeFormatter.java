package com.mango.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import net.sf.json.*;

/**
 * JSON字符串反序列化工具，用于解析已有约定的消息格式
 * @author 芒果君
 * @since 2018-03-26
 * @version 1.0
 */

public class JsonDecodeFormatter {
	
	
	/**
	 * 当data数据段为对象形态时，使用本方法	
	 * @param str JSON字符串
	 * @return  解析数据以hashMap形式返回，请使用键值对方式取数据；对于data部分，请使用“data”作为关键字取，
	 *          其对应的值类型是HashMap<String,String>
	 */
	@SuppressWarnings({ "rawtypes" })
	public static HashMap<String, Object> decodeDataObject(String str){
		HashMap<String, Object> eHashMap = new HashMap<>();
		JSONObject root = JSONObject.fromObject(str);
		
		Iterator keys = root.keys();
		
		while(keys.hasNext()) {
			String k = (String) keys.next();
			Object v = root.get(k);
			if (v instanceof JSONObject) {
				JSONObject jsonObject = root.getJSONObject(k);
				System.out.println(jsonObject.toString());
				HashMap<String, String> objMap = new HashMap<>();
				Iterator jsonObjectIterator = jsonObject.keys();
				while(jsonObjectIterator.hasNext()) {
					String _k = (String) jsonObjectIterator.next();
					String _v = (String) jsonObject.get(_k);
					objMap.put(_k, _v);
				}
				eHashMap.put(k, objMap);
			}
			else {
				v = root.getString(k);
				eHashMap.put(k, v);
			}
		}
		
		return eHashMap;
	}
	
	/**
	 * 当data数据段为数组形态时，使用本方法	
	 * @param str JSON字符串
	 * @return  解析数据以hashMap形式返回，请使用键值对方式取数据；对于data部分，请使用“data”作为关键字取，
	 *          其对应的值类型是ArrayList<HashMap<String,String>>,即在数组ArrayList内部嵌套HashMap      
	 */
	@SuppressWarnings("rawtypes")
	public static HashMap<String, Object> decodeDataArray(String str){
		HashMap<String, Object> eHashMap = new HashMap<>();
		JSONObject root = JSONObject.fromObject(str);
		
		Iterator keys = root.keys();
		
		while(keys.hasNext()) {
			String k = (String) keys.next();
			Object v = root.get(k);
			if (v instanceof JSONArray) {
				JSONArray jsonArray = root.getJSONArray(k);
				System.out.println(jsonArray.toString());
				ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
				Iterator jsonArrayIterator = jsonArray.iterator();
				while(jsonArrayIterator.hasNext()) {
					JSONObject obj = (JSONObject) jsonArrayIterator.next();
					Iterator objIterator = obj.keys();
					HashMap<String, String> objMap = new HashMap<>();
					while(objIterator.hasNext()) {
						String _k = (String) objIterator.next();
						String _v = (String) obj.get(_k);
						objMap.put(_k, _v);
					}
					arrayList.add(objMap);
				}
				eHashMap.put(k, arrayList);
			}
			else {
				v = root.getString(k);
				eHashMap.put(k, v);
			}
		}
		
		return eHashMap;
		

	}
	
}
