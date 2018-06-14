package com.mango.servlet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.logging.SimpleFormatter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mango.aliyun.AliyunPushManager;
import com.mango.entity.ARPackage;
import com.mango.entity.Record;
import com.mango.entity.Room;
import com.mango.entity.TeamPool;
import com.mango.entity.User;
import com.mango.entityManager.ARPackageManager;
import com.mango.entityManager.RecordManager;
import com.mango.entityManager.RoomManager;
import com.mango.entityManager.TeamPoolManager;
import com.mango.entityManager.UserManager;
import com.mango.utils.JsonDecodeFormatter;
import com.mango.utils.JsonEncodeFormatter;
import com.mango.utils.POST2String;

/**
 * Servlet implementation class RoomServlet
 */
public class RoomServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RoomServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	// TODO Auto-generated method stub
    		resp.sendRedirect("http://120.78.177.77/error.html");
    	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setHeader("Content-Type", "application/json;charset=utf8");
		//获取POST报文，并转换为字符串
		String rootStr = POST2String.convert(request.getInputStream());
		System.out.println(rootStr);
		
		//解析JSON字符串
		HashMap<String, Object> params = JsonDecodeFormatter.decodeDataObject(rootStr);
		String code = (String) params.get("code");
		HashMap<String, String> data = (HashMap<String, String>) params.get("data");
		String token = data.get("token");
		UserManager userManager = new UserManager();
		if(userManager.checkToken(token)) {
			if("10007".compareTo(code)==0) {
				//创建房间
				createRoom(data, response);
			}
			else if("10008".compareTo(code)==0) {
				//销毁房间
				destroyRoom(data, response);
			}
			else if("10011".compareTo(code)==0) {
				callForGameStart(data, response);
			}
			else if("10014".compareTo(code)==0) {
				callForGameEnd(data, response);
			}
			else {
				response.getWriter().write(JsonEncodeFormatter.universalResponse(90001, "Invalid Request Code."));
				return;	
			}
			
		}
		else {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90009, "Token Invalid or expired."));
			return;
		}

	}
	
	/**
	 * 请求创建房间
	 * @param data
	 * @param response
	 * @throws IOException 
	 */
	@SuppressWarnings("unused")
	private void createRoom(HashMap<String, String> data,HttpServletResponse response) throws IOException {
		String uuid = data.get("uuid").trim();
		RoomManager roomManager = new RoomManager();
		Room newRoom = roomManager.toCreate(uuid);
		TeamPoolManager teamPoolManager = new TeamPoolManager();
		teamPoolManager.addUser(newRoom.getUid(), uuid);
		if(newRoom!=null) {
			HashMap<String, String> result = new HashMap<>();
			result.put("roomUid", newRoom.getUid());
			result.put("joinCode", newRoom.getJoinCode());
			result.put("joinCode_expire_t", newRoom.getJoinCode_expire_t());
			result.put("state", String.valueOf(newRoom.getState()));
			result.put("created_t", newRoom.getCreated_t());
			response.getWriter().write(JsonEncodeFormatter.parser(0, result));
			return;
		}
		else {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90010, "Server Busy."));
			return;
		}
	}
	
	private void destroyRoom(HashMap<String, String> data,HttpServletResponse response) throws IOException{
		String uuid = data.get("uuid");
		String roomUid = data.get("roomUid");
		//1.先解散队伍，并获取房间内所有人的推送id
		TeamPoolManager teamPoolManager = new TeamPoolManager();
		String pushIDs = teamPoolManager.getCurrentMembersPushIDs(roomUid);
		System.out.println(pushIDs);
		try {
			AliyunPushManager aliyunPushManager = new AliyunPushManager();
			if(teamPoolManager.toDismiss(roomUid)) {
				aliyunPushManager.pushMessageToAndroid("房主解散了房间", "房主解散了房间", pushIDs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		//2.解除房主和房间的关系
		RoomManager roomManager = new RoomManager();
		if(roomManager.toDestroy(uuid, roomUid)==true) {
			response.getWriter().write(JsonEncodeFormatter.defaultSuccessfulResponse());
			return;
		}
		else {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90010, "Server Busy."));
			return;
		}
		
	}
	
	private void callForGameStart(HashMap<String, String> data,HttpServletResponse response) throws IOException {
		String roomUid = data.get("roomUid");
		String duration = data.get("duration");
		
		RoomManager roomManager = new RoomManager();
		Room room = roomManager.getByUid(roomUid);
		if(room==null) {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90012, "Invalid Room Uid."));
			return;
		}
		
		String start_t = String.valueOf(System.currentTimeMillis()/1000);
		TeamPoolManager teamPoolManager = new TeamPoolManager();
		if(roomManager.setStart(roomUid)&&teamPoolManager.updateStartTime(roomUid, start_t)) {
			
			
			//*******************************************
			//处理广播消息成员
			//获取当前房间中所有成员的基本信息
			//所有成员的pushID拼接的字符串
			String pushIdStr = teamPoolManager.getCurrentMembersPushIDs(room.getUid());
			
			//********************************************
			//处理AR信物信息汇总
			ARPackageManager arPackageManager = new ARPackageManager();
			ArrayList<ARPackage> arpacks  = arPackageManager.getAllInRoom(roomUid);
			ArrayList<Map<String, String>> arpacks_transferred = new ArrayList<>();
			Iterator<ARPackage> arIterator = arpacks.iterator();
			while(arIterator.hasNext()) {
				arpacks_transferred.add(arIterator.next().toHashMap());
			}
			
			HashMap<String, String> addition = new HashMap<>();
			addition.put("duration", duration);
			
			String info = JsonEncodeFormatter.parser(0,addition, arpacks_transferred);
			
			
			//通知所有的用户更新当前房间内的成员视图
			try {
				AliyunPushManager aliyunPushManager = new AliyunPushManager();
				aliyunPushManager.pushMessageToAndroid("游戏开始了噢",info,pushIdStr);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				response.getWriter().write(JsonEncodeFormatter.universalResponse(90010, "Server Busy."));
				return;
			}
			response.getWriter().write(JsonEncodeFormatter.universalResponse(0, "success."));
		}
		else {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90010, "Server Busy."));
			return;
		}
	}
	
	private void callForGameEnd(HashMap<String, String> data,HttpServletResponse response) throws IOException{
		String roomUid = data.get("roomUid");
		String uuid = data.get("uuid");
		
		TeamPoolManager teamPoolManager = new TeamPoolManager();
		UserManager userManager = new UserManager();
		RoomManager roomManager = new RoomManager();
		ARPackageManager arPackageManager = new ARPackageManager();
		RecordManager recordManager = new RecordManager();
		
		
		//获取排行榜，并返回给用户
		if(arPackageManager.uninstallPacks(roomUid)&&roomManager.setReady(roomUid)) {
			HashMap<User, Integer> result = teamPoolManager.getResultRank(roomUid);
			if(result!=null) {
				String pushids = teamPoolManager.getCurrentMembersPushIDs(roomUid);
				System.out.println(new SimpleDateFormat().format(new Date())+"\n pushids = "+pushids);
				
				//返回排行榜
				ArrayList<Map<String, String>> rankInfo = new ArrayList<>();
				Iterator<User> userIterator = result.keySet().iterator();
				while(userIterator.hasNext()) {
					User curUser = userIterator.next();
					HashMap<String, String> user_addition_info = curUser.toSecureHashMap();
					int timeInterval = result.get(curUser).intValue();
					user_addition_info.put("time", String.valueOf(timeInterval));
					System.out.println(new SimpleDateFormat().format(new Date()) + "\n userInfo = "+user_addition_info.toString());
					rankInfo.add(user_addition_info);
				}
				
				//将排行榜广播给每个用户
				String pushBody = JsonEncodeFormatter.parser(0, rankInfo);
				System.out.println(new SimpleDateFormat().format(new Date()) + "\n pushBody = "+pushBody);
				
				try {
					AliyunPushManager aliyunPushManager = new AliyunPushManager();
					aliyunPushManager.pushMessageToAndroid("排行榜来咯", pushBody, pushids);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					return;
				}
				
				//后续操作：将每个用户的数据写入记录表
				ArrayList<TeamPool> teamPools = teamPoolManager.getByRoomUid(roomUid);
				Iterator<TeamPool> teamPoolIterator = teamPools.iterator();
				while(teamPoolIterator.hasNext()) {
					TeamPool cur = (TeamPool) teamPoolIterator.next();
					
					//生成新的记录，写入记录表
					Record record = new Record();
					record.setUid(UUID.randomUUID().toString());
					record.setUserid(cur.getUserid());
					record.setStart_t(cur.getStart_t());
					record.setEnd_t(cur.getDone_t());
					record.setStepCount(cur.getStepCount());
					record.setArCount(cur.getArCount());
					recordManager.insertNewRecord(record);
					
					//用户表，更新用户的累计运动信息（AR信息，步数）
					userManager.updateEventSummary(cur.getUserid(), cur.getArCount(), cur.getStepCount());
				}
				
				teamPoolManager.toDismiss(roomUid);
				roomManager.toDestroy(uuid, roomUid);
				
				response.getWriter().write(JsonEncodeFormatter.universalResponse(0, "Success.Team pool will soon be dismissed"));
				return;
			}
			else {
				response.getWriter().write(JsonEncodeFormatter.universalResponse(90010, "Server Busy."));
				return;
			}
		}
		
	}

}
