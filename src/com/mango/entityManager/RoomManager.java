package com.mango.entityManager;

import java.util.UUID;
import com.mango.dao.DAO;
import com.mango.dao.RoomDAO;
import com.mango.entity.Room;
import com.mango.utils.AuthCodeCreator;

public class RoomManager extends DAO<Room> implements RoomDAO {

	private Room room;

	/**
	 * 创建房间
	 */
	@Override
	public Room toCreate(String uuid) {
		// TODO Auto-generated method stub
		String roomUid = UUID.randomUUID().toString();
		String joinCode;
		String sql_queryJoinCode;
		
		//对生成的邀请码进行查重,
		while(true) {
			joinCode = AuthCodeCreator.create();
			sql_queryJoinCode = "select * from ROOM where joinCode='"+joinCode+"';";
			room = super.get(sql_queryJoinCode);
			if(room==null) {
				break;
			}
			
		}
		long joinCode_expire_t = System.currentTimeMillis()/1000+15*60;
		String sql = "insert into ROOM values ('"+roomUid+"','"+joinCode+"','"+joinCode_expire_t+"',0,0,'"+uuid+"','"+System.currentTimeMillis()/1000+"')";
		try {
			super.update(sql);
			Room newRoom = new Room();
			newRoom.setUid(roomUid);
			newRoom.setJoinCode(joinCode);
			newRoom.setJoinCode_expire_t(String.valueOf(joinCode_expire_t));
			newRoom.setState(0);
			newRoom.setCreated_t(String.valueOf(System.currentTimeMillis()/1000));
			
			return newRoom;
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 销毁房间
	 */
	@Override
	public boolean toDestroy(String uuid,String roomUid) {
		// TODO Auto-generated method stub
		String sql = "delete from ROOM where organizerid='"+uuid+"' and uid='"+roomUid+"';";
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
	public Room getByJoinCode(String joinCode) {
		// TODO Auto-generated method stub
		String sql = "select * from ROOM where joinCode = '"+joinCode+"';";
		try {
			Room room = super.get(sql);
			return (room!=null)?room:null;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Room getByUid(String roomUid) {
		// TODO Auto-generated method stub
		String sql = "select * from ROOM where uid='"+roomUid+"';";
		try {
			return super.get(sql);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean setStart(String roomUid) {
		// TODO Auto-generated method stub
		String sql = "update ROOM set state=1 where uid='"+roomUid+"';";
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
	public boolean setReady(String roomUid) {
		// TODO Auto-generated method stub
		String sql = "update ROOM set state=0 where uid='"+roomUid+"';";
		try {
			super.update(sql);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}

}
