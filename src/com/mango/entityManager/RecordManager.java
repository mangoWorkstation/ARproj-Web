package com.mango.entityManager;

import java.util.ArrayList;
import java.util.UUID;

import com.mango.dao.DAO;
import com.mango.dao.RecordDAO;
import com.mango.entity.Record;

public class RecordManager extends DAO<Record> implements RecordDAO {

	@Override
	public boolean insertNewRecord(Record record) {
		// TODO Auto-generated method stub
		String uid = UUID.randomUUID().toString();
		String sql = "insert into RECORD values('"+uid+"','"+record.getUserid()+"','"+record.getStart_t()+"','"+record.getEnd_t()+"',"+record.getStepCount()+","+record.getArCount()+")";
		try {
			super.update(sql);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public ArrayList<Record> getByUser(String uuid) {
		String sql = String.format("select * from RECORD where userid = '%s'; ", uuid);
		return (ArrayList<Record>) super.getForList(sql);
	}

	@Override
	public ArrayList<Record> getByTime(String from_t, String to_t, int limit,String uuid) {
		if(limit<0) {
			String sql = String.format("select * from RECORD where userid = '%s' and start_t => '%s' and end_t <= '%s' order by start_t;", uuid,from_t,to_t);
			return (ArrayList<Record>) super.getForList(sql);

		}
		else {
			String sql = String.format("select * from RECORD where userid = '%s' and start_t => '%s' and end_t <= '%s' order by start_t desc limit %d;", uuid,from_t,to_t,limit);
			return (ArrayList<Record>) super.getForList(sql);
		}
	}

}
