package com.mango.utils;

import java.util.Random;

/**
 * 生成加密盐的工具，字符串长度默认为10
 * @author 芒果君
 * @since 2018-03-26
 * @version 1.0
 *
 */
public class SaltCreator {
	private static String[] alphabet = {
			"a","b","c","d","e","f","g","h","i","j",
			"k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z",
			"0","1","2","3","4","5","6","7","8","9"
	};
	/**
	 * 
	 * @return 长度为10的随机字符串，用于生成加密盐
	 */
	public static String create() {
		String str = "";
		Random random = new Random();
		for(int index=0;index<10;index++) {
			int ramdomNum = random.nextInt(35);
			str+=alphabet[ramdomNum];
		}
		return str;
	}
}
