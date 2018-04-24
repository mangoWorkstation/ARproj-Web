package com.mango.dao;

import java.util.ArrayList;
import java.util.HashMap;

import com.mango.entity.TeamPool;
import com.mango.entity.User;



public interface TeamPoolDAO {
	
	//通用方法，将用户加入队伍池，建立多对多关系
	public boolean addUser(String roomUid,String uuid);
	
	//将单个用户从队伍池中解除
	public boolean releaseUser(String roomUid,String uuid);
	
	
	//解散队伍，并获得该房间内的所有参赛成员的pushID，用于推送通知
	public boolean toDismiss(String roomUid);
	
	//获得当前房间内所有成员的uuid,基本信息，pushID
	public ArrayList<User> getCurrentMembers(String roomUid);
	
	//更新游戏的开始时间
	public boolean updateStartTime(String roomUid,String start_t);
	
	//用户上报完成进度
	public boolean updateGamePercent(String roomUid,String uuid,float percent,int arCount,int stepCount);
	
	//用户上报已经完成所有任务，记录时间戳
	public boolean updateDoneTime(String roomUid,String uuid,String done_t,int arCount,int stepCount);
	
	//获取当前房间内的所有成员的pushids，拼接后的字符串
	public String getCurrentMembersPushIDs(String roomUid);
	
	//游戏结束后，返回统计后的排行榜，此数据结构内包含 用户基本信息：uuid，姓名，（头像），推送id，总计时（单位为秒）
	public HashMap<User,Integer> getResultRank(String roomUid);
	
	//通过房间id获得队伍池中的所有关系
	public ArrayList<TeamPool> getByRoomUid(String roomUid);
	
}
