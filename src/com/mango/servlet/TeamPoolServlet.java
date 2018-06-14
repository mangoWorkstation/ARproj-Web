package com.mango.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.mango.aliyun.AliyunPushManager;
import com.mango.entity.Room;
import com.mango.entity.User;
import com.mango.entityManager.RoomManager;
import com.mango.entityManager.TeamPoolManager;
import com.mango.entityManager.UserManager;
import com.mango.utils.JsonDecodeFormatter;
import com.mango.utils.JsonEncodeFormatter;
import com.mango.utils.POST2String;

/**
 * Servlet implementation class TeamPoolServlet
 */
public class TeamPoolServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TeamPoolServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.sendRedirect("http://120.78.177.77/error.html");
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
			if("10009".compareTo(code)==0) {
				toJoinByCode(params, response);
			}
			else if("10012".compareTo(code)==0) {
				toUpdateGamePercent(data, response);
			}
			else if("10013".compareTo(code)==0) {
				toCallForFinish(data, response);
			}
			else if("10016".compareTo(code)==0) {
				toQuit(data,response);
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
	 * 本方法只干两件事
	 * 1.将当前房间信息和房间内所有成员的信息，通知请求加入的客户端，用于载入当前界面
	 * 2.推送通知，广播告诉房间内所有成员的客户端，更新当前视图
	 * @param params
	 * @param response
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private void toJoinByCode(HashMap<String, Object> params,HttpServletResponse response) throws IOException {
		HashMap<String, String> data = (HashMap<String, String>) params.get("data");
		String joinCode = data.get("joinCode");
		String uuid = data.get("uuid");
		
		//校验邀请码，并查找对应的房间
		RoomManager roomManager = new RoomManager();
		Room room = roomManager.getByJoinCode(joinCode);
		if(room==null) {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90011, "Invalid Join Code."));
			return;
		}
		
		TeamPoolManager teamPoolManager = new TeamPoolManager();
		//校验邀请码成功，在队伍池中添加新成员的关联
		if(teamPoolManager.addUser(room.getUid(), uuid)) {
			HashMap<String, String> roomInfo = room.toHashMap();
			
			//获取当前房间中所有成员的基本信息
			ArrayList<User> users = teamPoolManager.getCurrentMembers(room.getUid());
			
			//所有成员的pushID拼接的字符串
			String pushIdStr = teamPoolManager.getCurrentMembersPushIDs(room.getUid());
			ArrayList<Map<String, String>> data_array = new ArrayList<>();
			Iterator<User> userIterator = users.iterator();
			while(userIterator.hasNext()) {
				User _user = userIterator.next(); 
				data_array.add(_user.toSecureHashMap());
			}
			
			//信息炒鸡大汇总
			String updateFeedBackStr = JsonEncodeFormatter.parser(0, roomInfo, data_array);
			
			//通知所有的用户更新当前房间内的成员视图
			try {
				AliyunPushManager aliyunPushManager = new AliyunPushManager();
				aliyunPushManager.pushMessageToAndroid("有成员变动",updateFeedBackStr,pushIdStr);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			response.getWriter().write(updateFeedBackStr);
			return;
		}
		else {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90010, "Server Busy."));
			return;
		}
	}
	
	
	private void toUpdateGamePercent(HashMap<String, String> data,HttpServletResponse response) throws IOException {
		String roomUid = data.get("roomUid");
		String uuid = data.get("uuid");
		float percent = Float.valueOf(data.get("percent"));
		String arCount = data.get("arCount");
		String stepCount = data.get("stepCount");
		
		
		if(percent<0||percent>1) {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90006, "Illegal parameters.Check Again."));
			return;
		}
		
		TeamPoolManager teamPoolManager = new TeamPoolManager();
		
		if(teamPoolManager.updateGamePercent(roomUid, uuid, percent,Integer.valueOf(arCount),Integer.valueOf(stepCount))) {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(0, "success."));
			return;
		}
		else {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90010, "Server Busy."));
			return;
		}
	}
	
	private void toCallForFinish(HashMap<String, String> data,HttpServletResponse response) throws IOException {
		String roomUid = data.get("roomUid");
		String uuid = data.get("uuid");
		String arCount = data.get("arCount");
		String stepCount = data.get("stepCount");
//		String start_t = data.get("start_t");
		String end_t = data.get("end_t");
		
		
		TeamPoolManager teamPoolManager = new TeamPoolManager();
//		RecordManager recordManager = new RecordManager();
//		UserManager userManager = new UserManager();
		
		if(teamPoolManager.updateDoneTime(roomUid, uuid, end_t,Integer.valueOf(arCount),Integer.valueOf(stepCount))) {
//			Record record = new Record();
//			record.setUid(UUID.randomUUID().toString());
//			record.setUserid(uuid);
//			record.setStart_t(start_t);
//			record.setEnd_t(end_t);
//			record.setArCount(Integer.valueOf(arCount));
//			record.setStepCount(Integer.valueOf(stepCount));
//			if(recordManager.insertNewRecord(record)) {
//				if(userManager.updateEventSummary(uuid, Integer.valueOf(arCount),Integer.valueOf(stepCount))) {
//					
//					//广播完成通知
					//获取当前房间中所有成员的基本信息					
					String pushIdStr = teamPoolManager.getCurrentMembersPushIDs(roomUid);
					
					//通知所有的用户更新当前房间内的成员视图
					try {
						AliyunPushManager aliyunPushManager = new AliyunPushManager();
						aliyunPushManager.pushNoticeToAndroid("有成员完成了噢！","加快速度啊啊啊！",pushIdStr);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						response.getWriter().write(JsonEncodeFormatter.universalResponse(90010,"Server Busy"));
						return;
					}
					response.getWriter().write(JsonEncodeFormatter.universalResponse(0,"Success.Please tell user wait others to finished."));
					return;
//				}
//			}
			
		}
	}
	
	private void toQuit(HashMap<String, String> data,HttpServletResponse response) throws IOException {
		String uuid = data.get("uuid");
		String roomUid = data.get("roomUid");
		TeamPoolManager teamPoolManager = new TeamPoolManager();
		
		if(teamPoolManager.releaseUser(roomUid, uuid)) {
			//广播完成通知
			//获取当前房间中所有成员的基本信息					
			String pushIdStr = teamPoolManager.getCurrentMembersPushIDs(roomUid);
			
			//获取当前房间中所有成员的基本信息
			ArrayList<User> users = teamPoolManager.getCurrentMembers(roomUid);
			
			//所有成员的pushID拼接的字符串
			ArrayList<Map<String, String>> data_array = new ArrayList<>();
			Iterator<User> userIterator = users.iterator();
			while(userIterator.hasNext()) {
				User _user = userIterator.next(); 
				data_array.add(_user.toSecureHashMap());
			}
			
			RoomManager roomManager = new RoomManager();
			Room room = roomManager.getByUid(roomUid);
			HashMap<String, String> roomInfo = room.toHashMap();
			
			//信息炒鸡大汇总
			String updateFeedBackStr = JsonEncodeFormatter.parser(0, roomInfo, data_array);
			
			//通知所有的用户更新当前房间内的成员视图
			try {
				AliyunPushManager aliyunPushManager = new AliyunPushManager();
				aliyunPushManager.pushMessageToAndroid("有成员变动",updateFeedBackStr,pushIdStr);
			} catch (Exception e) {
				e.printStackTrace();
				response.getWriter().write(JsonEncodeFormatter.universalResponse(90010,"Server Busy"));
				return;
			}

			response.getWriter().write(JsonEncodeFormatter.universalResponse(0, "Quit OK."));
			return;
		}
		else {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90010, "Server Busy."));
			return;
		}
	}

}
