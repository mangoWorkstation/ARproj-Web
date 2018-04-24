package com.mango.dao;

import java.util.ArrayList;

import com.mango.entity.Record;

public interface RecordDAO {
	
	//新增运动记录，事件发生的时间仅在用户上报完成全部游戏之后
	public boolean insertNewRecord(Record record);
	
	//根据单个用户获取运动记录
	public ArrayList<Record> getByUser(String uuid);
	
	//按起止时间查询记录
	public ArrayList<Record> getByTime(String from_t,String to_t,int limit,String uuid);
}
