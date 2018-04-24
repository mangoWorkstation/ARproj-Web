package com.mango.dao;

import java.util.HashMap;

import com.mango.entity.User;

public interface UserDAO {
	//PART 1:首次注册用
	public boolean registerNewUser(String tel,String authCode);
	public boolean verifyRegister(String tel,String authCode);
	public String forcedPresetPassword(String passwordSHA512,String tel);
	public boolean updateUserProfile(User user,String token);
	
	//PART 2:登录用
	public HashMap<String, String> loginWithSHApwd(String SHApwd,String tel,String pushID);
	public boolean loginWithToken(String token,String pushID);
	
	//PART 3:每次操作验证token用
	public boolean checkToken(String token);
	
	
	//用于每次运动后，更新用户的累计数据
	public boolean updateEventSummary(String uuid,int arCount,int stepCount);
	
	//根据单个uuid 或 token 获取用户的基本信息，此方法可查询本人信息或其他用户的基本信息
	//key = token || key = uuid
	public User getBasicProfile(String key);
	
	//更新用户的短信验证码和验证有效时间两个字段，这两个字段不为空时表明，用户正在进行操作验证的窗口期；数据库内部会定时将过期的验证码清除为null；
	//这两个字段长期设置为空
	public boolean updateAuthCode(String authCode,String token);
	
	//校验验证码的正确性
	public boolean verifyAuthCode(String authCode,String token);
	
	//变更更新的手机号码
	public boolean updateTel(String token,String tel);
	
	//重置密码
	public boolean resetPassword(String SHAPwd,String token);
}
