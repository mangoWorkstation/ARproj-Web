package com.mango.dao;

import com.mango.entity.Room;

public interface RoomDAO {
	//创建房间
	public Room toCreate(String uuid);
	
	//销毁房间，目前为降低思考的复杂度，当房主离开时，即视房间被销毁，系统推送通知其他成员离开，app内部弹窗提示参与成员离开
	//暂不考虑转让房主，后续再说
	public boolean toDestroy(String uuid,String roomUid);
	
	//根据邀请码查询房间
	public Room getByJoinCode(String joinCode);
	
	//根据房间id查询
	public Room getByUid(String roomUid);
	
	//房主宣布游戏开始
	public boolean setStart(String roomUid);
	
	//房主宣布游戏结束（重回准备状态）
	public boolean setReady(String roomUid);
}
