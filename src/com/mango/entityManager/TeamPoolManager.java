package com.mango.entityManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import com.mango.dao.DAO;
import com.mango.dao.TeamPoolDAO;
import com.mango.db.SqlManager;
import com.mango.entity.TeamPool;
import com.mango.entity.User;

public class TeamPoolManager extends DAO<TeamPool> implements TeamPoolDAO {

	@Override
	public boolean toDismiss(String roomUid) {
		
//		ArrayList<String> pushIDs = new ArrayList<>();
//		//左联表查询参赛成员的pushID
//		String sql_query = "select * from TEAMPOOL left join USER on TEAMPOOL.userid=USER.uuid where TEAMPOOL.roomid='"+roomUid+"';";
//		ResultSet resultSet;
//		try {
//			resultSet = SqlManager.getInstance().executeSqlQuery(sql_query);
//			while(resultSet.next()) {
//				pushIDs.add(resultSet.getString("pushID"));
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
		String sql_delete = "delete from TEAMPOOL where roomid='"+roomUid+"';";
		try {
			super.update(sql_delete);
			return true;
		} catch (Exception e) {
			return false;
		}		
	}

	@Override
	public boolean addUser(String roomUid,String uuid) {
		try {
			String pk = UUID.randomUUID().toString();
			String sql_add = "insert into TEAMPOOL values('"+pk+"','"+roomUid+"','"+uuid+"',0.0,null,null,0,0);";
			super.update(sql_add);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Override
	public boolean releaseUser(String roomUid,String uuid) {
		String sql = String.format("delete from TEAMPOOL where roomid = '%s' and userid = '%s';", roomUid,uuid);
		try {
			super.update(sql);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public ArrayList<User> getCurrentMembers(String roomUid) {
//		String sql = "select * from TEAMPOOL where roomid = '"+roomUid+"';";
//		List<TeamPool> teampool = super.getForList(sql);
//		ArrayList<String> uuid = new ArrayList<>();
//		Iterator<TeamPool> teampoolIterator = teampool.iterator();
//		while(teampoolIterator.hasNext()) {
//			uuid.add(teampoolIterator.next().getUserid());
//		}
//		return uuid;
		
		ArrayList<User> users = new ArrayList<>();
		//左联表查询参赛成员的pushID
		String sql_query = "select * from TEAMPOOL left join USER on TEAMPOOL.userid=USER.uuid where TEAMPOOL.roomid='"+roomUid+"';";
		ResultSet resultSet;
		try {
			resultSet = SqlManager.getInstance().executeSqlQuery(sql_query);
			while(resultSet.next()) {
				User user = new User();
				user.setUuid(resultSet.getString("uuid"));
				user.setPushID(resultSet.getString("pushID"));
				user.setName(resultSet.getString("name"));
				users.add(user);
			}
			return users;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean updateGamePercent(String roomUid, String uuid,float percent,int arCount,int stepCount) {
		String sql = "update TEAMPOOL set percent = "+percent+",ARCount = "+arCount+",stepCount="+stepCount+" where roomid = '"+roomUid+"' and userid = '"+uuid+"';";
		try {
			super.update(sql);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean updateDoneTime(String roomUid, String uuid, String done_t,int arCount,int stepCount) {
		String sql = "update TEAMPOOL set done_t = '"+done_t+"',percent=1,ARCount = "+arCount+",stepCount="+stepCount+" where roomid = '"+roomUid+"' and userid = '"+uuid+"';";
		try {
			super.update(sql);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public String getCurrentMembersPushIDs(String roomUid) {
		ArrayList<User> users = this.getCurrentMembers(roomUid);
		//所有成员的pushID拼接的字符串
		String pushIdStr = "";
//		ArrayList<Map<String, String>> data_array = new ArrayList<>();
		Iterator<User> userIterator = users.iterator();
		while(userIterator.hasNext()) {
			User _user = userIterator.next(); 
//			data_array.add(_user.toHashMap());
			pushIdStr += _user.getPushID()+",";
		}
		//去除最后一个逗号
		char[] pushIdCh = pushIdStr.toCharArray();
		int lastIndex = pushIdCh.length;
		pushIdCh[lastIndex-1] = ' ';
		pushIdStr = String.valueOf(pushIdCh);
		return pushIdStr.trim();
	}

	@Override
	public HashMap<User,Integer> getResultRank(String roomUid) {
		//左联表查询用户表信息
		String sql_query = "select * from TEAMPOOL left join USER on TEAMPOOL.userid=USER.uuid where TEAMPOOL.roomid='"+roomUid+"';";
		ResultSet resultSet;
		
		//用于保存用户信息和所用时间（单位秒）
		HashMap<User,Integer> rank = new HashMap<>();
		try {
			resultSet = SqlManager.getInstance().executeSqlQuery(sql_query);
			while(resultSet.next()) {
				User user = new User();
				user.setUuid(resultSet.getString("uuid"));
				user.setPushID(resultSet.getString("pushID"));
				user.setName(resultSet.getString("name"));		
				
				Integer timeInterval = null;
				if(resultSet.getString("done_t")==null) {
					//计算所用时间
					timeInterval = new Integer(Integer.valueOf(String.valueOf(System.currentTimeMillis()/1000))-Integer.valueOf(resultSet.getString("start_t")));
					
				}
				else {
					timeInterval = new Integer(Integer.valueOf(String.valueOf(resultSet.getString("done_t")))-Integer.valueOf(resultSet.getString("start_t")));
				}
			
//				HashMap<User, Integer> r = new HashMap<>();
				rank.put(user, timeInterval);
			}
			
			ArrayList<Map.Entry<User, Integer>> list = new ArrayList<>(rank.entrySet());
			
			
			//根据所用时长，对用户进行排序
			Collections.sort(list, new Comparator<Map.Entry<User, Integer>>() {

				@Override
				public int compare(Entry<User, Integer> o1, Entry<User, Integer> o2) {
					return o1.getValue().compareTo(o2.getValue());
				}
			});
			
			
			System.out.println(new SimpleDateFormat().format(new Date())+"\n rank"+rank.toString());
			
			return rank;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
	}

	@Override
	public ArrayList<TeamPool> getByRoomUid(String roomUid) {
		try {
			String sql = String.format("select * from TEAMPOOL where roomid = '%s';", roomUid);
			return (ArrayList<TeamPool>) super.getForList(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}

	@Override
	public boolean updateStartTime(String roomUid, String start_t) {
		String sql = String.format("update TEAMPOOL set start_t = '%s' where roomid = '%s';", start_t,roomUid);
		try {
			super.update(sql);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	

}
