package com.mango.entityManager;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import com.mango.dao.ARPackageDAO;
import com.mango.dao.DAO;
import com.mango.entity.ARPackage;

public class ARPackageManager extends DAO<ARPackage> implements ARPackageDAO {

	@Override
	public boolean installPacks(String roomUid, ArrayList<ARPackage> arPackages) {
		// TODO Auto-generated method stub
		Iterator<ARPackage> iterator = arPackages.iterator();
		while(iterator.hasNext()) {
			ARPackage ar = iterator.next();
			String uuid = UUID.randomUUID().toString();
			String sql = "insert into AR values('"+uuid+"','"+roomUid+"',"+ar.getLatitude()+","+ar.getLongitude()+",'"+ar.getContent()+"');";
			try {
				super.update(sql);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	@Override
	public ArrayList<ARPackage> getAllInRoom(String roomUid) {
		// TODO Auto-generated method stub
		String sql = "select * from AR where roomid = '"+roomUid+"';";
		return (ArrayList<ARPackage>) super.getForList(sql);
	}

	@Override
	public boolean uninstallPacks(String roomUid) {
		// TODO Auto-generated method stub
		String sql = String.format("delete from AR where roomid = '%s';", roomUid);
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
