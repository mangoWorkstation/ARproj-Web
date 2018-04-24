package com.mango.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class POST2String {
	
	public static String convert(InputStream inputStream) {
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));
			String line = null;
			StringBuffer content = new StringBuffer();
			while((line = bufferedReader.readLine()) != null) {
				content.append(line);
			}
			return content.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
}
