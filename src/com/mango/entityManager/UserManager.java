package com.mango.entityManager;

import com.mango.dao.UserDAO;
import com.mango.entity.User;
import com.mango.utils.Encryptor;
import com.mango.utils.SaltCreator;

import java.util.HashMap;
import java.util.UUID;

import com.mango.dao.DAO;

public class UserManager extends DAO<User> implements UserDAO{

	
	/**
	 *	PART 1:用户首次注册时用
	 */
	
	
	/**
	 * 注册新用户用,请求注册手机验证短信
	 */
	@Override
	public boolean registerNewUser(String tel,String authCode) {
		String uuid = UUID.randomUUID().toString();
		String salt = SaltCreator.create();
		long authCode_expire_t = System.currentTimeMillis()/1000 + 60;
		String sql_query = "select * from USER where tel='"+tel+"';";
		User aUser = super.get(sql_query);
		if(aUser==null) {
			String sql = "insert into USER values ('"+uuid+"','"+tel+"',null,'"+salt+"',null,null,null,null,null,null,null,null,null,null,null,0,0,0,0,'"+authCode+"','"+String.valueOf(authCode_expire_t)+"',null);";
			super.update(sql);
			return true;
		}
		return false;
	}
	
	/**
	 * 注册验证短信验证码用
	 * 1.对及时完成验证的用户，将其密码指纹appKey设为“registered”字段，使其不会被数据库定时任务清理
	 * 2.对没有按时完成验证的用户，系统将会3分钟后自动清理其验证信息
	 * 3.系统视appKey的值用于判定用户是否完成验证
	 * 4.完成验证后，请强制用户设置密码，随后是个人信息
	 */
	@Override
	public boolean verifyRegister(String tel, String authCode) {
		String sql = "select * from USER where tel='"+tel+"' and authCode='"+authCode+"';";
		User aUser = super.get(sql);
		if(aUser!=null) {
			String sql_1 = "update USER set appKey='registered',authCode=null,authCode_expire_t=null where tel='"+tel+"';";
			super.update(sql_1);
			return true;
		}
		return false;
	}
	
	/**
	 * 首次注册时，强制用户设定密码
	 */
	public String forcedPresetPassword(String passwordSHA512,String tel) {
		String sql_1 = "select * from USER where tel='"+tel+"' and appKey='registered'";
		User aUser = super.get(sql_1);
		if(aUser!=null) {
			String appKey = new Encryptor().encodeSHA512Salt(passwordSHA512, aUser.getSalt());
			String newToken = UUID.randomUUID().toString();
			long newTokenExpire_t = System.currentTimeMillis()/1000 + 7*24*60*60;
			String sql_2 = "update USER set appKey='"+appKey+"',token='"+newToken+"',token_expire_t='"+newTokenExpire_t+"' where tel='"+tel+"';";
			super.update(sql_2);
			return newToken;
		}
		return null;
	}

	/**
	 * 首次注册时，完善用户信息
	 */
	@Override
	public boolean updateUserProfile(User user,String token) {
		String sql = "update USER set name='"+user.getName()+"',gender="+user.getGender()+",age="+user.getAge()+",weight="+user.getWeight()+",height="+user.getHeight()+",province='"+user.getProvince()+"',city='"+user.getCity()+"' where token='"+token+"';";
		try {
			super.update(sql);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * PART 2:使用密码摘要SHApwd登录，仅在token失效时请求新的token，或首次登录
	 */
	@Override
	public HashMap<String, String> loginWithSHApwd(String SHApwd,String tel,String pushID) {
		String sql = "select * from USER where tel='"+tel+"';";
		User user = super.get(sql);
		if(user!=null) {
			String appKey_test = new Encryptor().encodeSHA512Salt(SHApwd, user.getSalt());
			System.out.println(SHApwd);
			System.out.println(user.getSalt());
			System.out.println(appKey_test);
			System.out.println(user.getAppKey());
			if(appKey_test.compareTo(user.getAppKey())==0) {
				String newToken = UUID.randomUUID().toString();
				long new_token_expire_t = System.currentTimeMillis()/1000 + 7*24*60*60;
				String sql_1 = "update USER set token='"+newToken+"',token_expire_t='"+new_token_expire_t+"',pushID='"+pushID+"' where tel='"+tel+"'";
				try {
					super.update(sql_1);
					HashMap<String, String> e = new HashMap<>();
					e.put("token", newToken);
					e.put("uuid", user.getUuid());
					return e;
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
			else {
				return null;
			}
		}
		return null;
	}

	/**
	 * 使用临时操作令牌登录
	 */
	@Override
	public boolean loginWithToken(String token,String pushID) {
		String sql = "select * from USER where token='"+token+"';";
		User user = super.get(sql);
		if(user!=null) {
			long new_token_expire_t = System.currentTimeMillis()/1000 + 7*24*60*60;
			String sql_1 = "update USER set token_expire_t='"+new_token_expire_t+"',pushID='"+pushID+"' where token = '"+token+"';";
			try {
				super.update(sql_1);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			
		}
		return false;
	}

	/**
	 * PART 3: 用于每次操作前验证临时操作令牌token
	 */
	@Override
	public boolean checkToken(String token) {
		String sql = "select * from USER where token='"+token+"';";
		User user = super.get(sql);
		if(user!=null) {
			return true;
		}
		return false;
	}

	@Override
	public boolean updateEventSummary(String uuid, int arCount, int stepCount) {
		String sql = "update USER set arCount = arCount + "+arCount+",stepCount = stepCount + "+stepCount+" where uuid = '"+uuid+"';";
		try {
			super.update(sql);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public User getBasicProfile(String key) {
		String sql = String.format("select * from USER where uuid = '%s' or token = '%s' or tel = '%s';", key,key,key);
		try {
			return super.get(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean updateAuthCode(String authCode,String token) {
		long authCode_expire_t = System.currentTimeMillis()/1000 + 60;
		String sql = String.format("update USER set authCode = '%s',authCode_expire_t = '%s' where token = '%s';", authCode,String.valueOf(authCode_expire_t),token);
		try {
			super.update(sql);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean verifyAuthCode(String authCode, String token) {
		String sql = String.format("select * from USER where authCode = '%s' and token = '%s';",authCode,token);
		User user = super.get(sql);
		return (user!=null)?true:false;
	}

	@Override
	public boolean updateTel(String token, String newTel) {
		String sql = String.format("update USER set tel = '%s' where token = '%s';", newTel,token);
		try {
			super.update(sql);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean resetPassword(String SHAPwd, String token) {
		User user = this.getBasicProfile(token);
		
		if(user == null) {
			return false;
		}
		
		//只更新appKey，不更新盐
		String appKey = new Encryptor().encodeSHA512Salt(SHAPwd, user.getSalt());
		String sql = String.format("update USER set appKey='%s'; where token = '%s';", appKey,token);
		
		try {
			super.update(sql);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}


}
