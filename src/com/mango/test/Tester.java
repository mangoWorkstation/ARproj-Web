package com.mango.test;

import java.awt.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.*;
import com.mango.aliyun.AliSmsSender;
import com.mango.aliyun.AliyunPushManager;
import com.mango.db.SqlManager;
import com.mango.entity.ARPackage;
import com.mango.entity.User;
import com.mango.entityManager.ARPackageManager;
import com.mango.entityManager.TeamPoolManager;
import com.mango.entityManager.UserManager;
import com.mango.utils.Encryptor;
import com.mango.utils.JsonDecodeFormatter;
import com.mango.utils.SaltCreator;

public class Tester {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		String aString = "123456";
//		String salt = SaltCreator.create();
//		System.out.println(salt);
//		System.out.println(new Encryptor().encodeSHA512Salt(aString, salt));
//		
//		System.out.println(System.currentTimeMillis());
//		
//		
//		String str = "{\n" + 
//				"    \"code\": \"10001\",\n" + 
//				"    \"timestamp\": \"172928372829\",\n" + 
//				"    \"token\":\"42e5015f-d7d9-4e96-89d5-43906bfa0abb\",\n" + 
//				"    \"helloworld\":\"121212121212\",\n" + 
//				"    \"data\": [\n" + 
//				"       {\n" + 
//				"        \"key1\": \"value1\",\n" + 
//				"        \"key2\": \"value2\",\n" + 
//				"        \"beach\":\"sun\"\n" + 
//				"       },\n" + 
//				"       {\n" + 
//				"        \"key3\": \"value3\",\n" + 
//				"        \"key4\": \"value4\",\n" + 
//				"        \"beach\":\"sun\"\n" + 
//				"       }\n" + 
//				"    ]\n" + 
//				"}";
//	    HashMap<String, Object> eHashMap = JsonDecodeFormatter.decodeDataArray(str);
//	    System.out.println(eHashMap.toString());
	    
//	    if(SqlManager.getInstance().connection.equals(null)==false) {
//	    		System.out.println(SqlManager.getInstance().connection.getHost());
//	    }
	    
//	    ARPackageManager arPackageManager = new ARPackageManager();
//	    ARPackage arPackage = arPackageManager.get("aaaa");
//	    System.out.println(arPackage.toString());
	    
	    
//		AliSmsSender.sendAuthCodeSms("15678866034", "233333");
		
	    
//	    List<ARPackage> list = arPackageManager.getAll();
//	    Iterator<ARPackage> iterator = list.iterator();
//	    while(iterator.hasNext()) {
//	    		ARPackage eArPackage = iterator.next();
//	    		System.out.println(eArPackage.toString());
//	    }
//	    
//	    System.out.println(SaltCreater.create());
	    
	    
//	    System.out.println(new Encryptor().SHA512("123456"));
//	    System.out.println(new Encryptor().encodeSHA512Salt("ba3253876aed6bc22d4a6ff53d8406c6ad864195ed144ab5c87621b6c233b548baeae6956df346ec8c17f5ea10f35ee3cbc514797ed7ddd3145464e2a0bab413", "lq3wepm8wz"));
	    
//	    AliyunPushManager aliyunPushManager;
//		try {
//			aliyunPushManager = new AliyunPushManager();
//		    aliyunPushManager.pushMessageToAndroid("hahaha", "mangguojun");
//
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	    
//	    TeamPoolManager teamPoolManager = new TeamPoolManager();
//	    String pushids = teamPoolManager.getCurrentMembersPushIDs("0000");
//	    System.out.println(pushids);
	    
//	  //去除最后一个逗号
//	    String pushIdStr = "hello,world,iiiu,";
//  		char[] pushIdCh = pushIdStr.toCharArray();
//  		int lastIndex = pushIdCh.length;
//  		pushIdCh[lastIndex-1] = ' ';
//  		pushIdStr = String.valueOf(pushIdCh);
//  		System.out.println(pushIdStr);
//	    
//	    String sql = String.format("delete from AR where roomid = '%s';", pushIdStr);
//	    System.out.println(sql);
	    
//	    ArrayList<Integer> eArrayList = new ArrayList<>();
//	    eArrayList.add(5);
//	    eArrayList.add(1);
//	    eArrayList.add(3);
//	    eArrayList.add(0);
//	    eArrayList.add(10);
//	    eArrayList.add(8);
//	    
//	    Collections.sort(eArrayList, new Comparator<Integer>() {
//
//			@Override
//			public int compare(Integer o1, Integer o2) {
//				// TODO Auto-generated method stub
//				return o1.compareTo(o2);
//			}
//		});
//	    
//	    System.out.println(eArrayList.toString());
	    
	    
	    	
//	    HashMap<String,Integer> aHashMap = new HashMap<>();
//	    aHashMap.put("a", 5);
//	    
//	    aHashMap.put("b", 1);
//	    
//	    aHashMap.put("c", 7);
//	    
//	    aHashMap.put("d", 4);
//	    
//	    aHashMap.put("e", 2);
//	    
//	    aHashMap.put("f", 0);
//	   
//	    ArrayList<Map.Entry<String, Integer>> list = new ArrayList<>(aHashMap.entrySet());
//	    System.out.println(aHashMap.toString());
//
//	    
//		//根据所用时长，对用户进行排序
//		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
//
//			@Override
//			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
//				// TODO Auto-generated method stub
//				
//				return o1.getValue().compareTo(o2.getValue());
//			}
//		});
//		
//		System.out.println(list.toString());
//			
//		
//		User user = new User();
//		user.setName("hello");
//		user.setAge(5);
//		System.out.println(user.toHashMap());
//		
//		System.out.println(user.toSecureHashMap());
//		
//		UserManager userManager = new UserManager();
//		
//		System.out.println(userManager.checkToken("aaaa"));
//		
//		System.out.println(userManager.getForList("select * from USER;").toString());
	    
		
		String uuid = UUID.randomUUID().toString();
		
		String shapwd = new Encryptor().SHA512("123456");
		
		String salt = SaltCreator.create();
		
		String appKey = new Encryptor().encodeSHA512Salt(shapwd, salt);
		
		System.out.println(uuid);
		System.out.println(shapwd);
		System.out.println(salt);
		System.out.println(appKey);
		
//		String sql = String.format("update DEVICE set air_temp = %f,air_humidity = %f,soil_temp = %f,soil_humidity = %f,",0.1,0.1,0.1,0.1);
//		System.out.println(sql);

	    
	}
	
	

}
