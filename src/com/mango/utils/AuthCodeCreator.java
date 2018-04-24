package com.mango.utils;

import java.util.Random;
/**
 * 生成长度为6的数字验证码
 * @author 芒果君
 * @since 2018-03-26
 * @version 1.0
 *
 */
public class AuthCodeCreator {
	private static String[] number = {
		"0","1","2","3","4","5","6","7","8","9"
	};
	/**
	 * 
	 * @return 长度为6的数字串
	 */
	public static String create() {
		String str = "";
		Random random = new Random();
		for(int index=0;index<6;index++) {
			int ramdomNum = random.nextInt(9);
			str+=number[ramdomNum];
		}
		return str;
	}
}
