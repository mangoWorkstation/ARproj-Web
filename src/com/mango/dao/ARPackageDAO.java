package com.mango.dao;

import java.util.ArrayList;

import com.mango.entity.ARPackage;

public interface ARPackageDAO {
	
	//房主用户设置AR信物
	public boolean installPacks(String roomUid, ArrayList<ARPackage> arPackages);
	
	//获取当前房间内所有的AR信物的信息
	public ArrayList<ARPackage> getAllInRoom(String roomUid);
	
	//删除本场游戏当中所有AR信物的信息
	public boolean uninstallPacks(String roomUid);
}
