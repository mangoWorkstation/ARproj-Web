package com.mango.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mango.entity.ARPackage;
import com.mango.entity.Room;
import com.mango.entityManager.ARPackageManager;
import com.mango.entityManager.RoomManager;
import com.mango.entityManager.UserManager;
import com.mango.utils.JsonDecodeFormatter;
import com.mango.utils.JsonEncodeFormatter;
import com.mango.utils.POST2String;

/**
 * Servlet implementation class ARPackServlet
 */
public class ARPackServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ARPackServlet() {
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
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setHeader("Content-Type", "application/json;charset=utf8");
		//获取POST报文，并转换为字符串
		String rootStr = POST2String.convert(request.getInputStream());
		System.out.println(rootStr);
		
		//解析JSON字符串
		HashMap<String, Object> params = JsonDecodeFormatter.decodeDataArray(rootStr);
		String code = (String) params.get("code");
		String token = (String) params.get("token");
		UserManager userManager = new UserManager();
		if(userManager.checkToken(token)) {
			if ("10010".compareTo(code)==0) {
				installARPacks(params,response);
			}
			else {
				response.getWriter().write(JsonEncodeFormatter.universalResponse(90001, "Invalid Request Code."));
				return;	
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void installARPacks(HashMap<String, Object> params,HttpServletResponse response) throws IOException {
		String roomUid = (String) params.get("roomUid");
		
		RoomManager roomManager = new RoomManager();
		Room room = roomManager.getByUid(roomUid);
		
		//检查房间是否存在
		if(room==null) {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90012, "Invalid Room Uid."));
			return;
		}
		
		ArrayList<HashMap<String,String>> data = (ArrayList<HashMap<String, String>>) params.get("data");
		Iterator<HashMap<String,String>> data_iterator = data.iterator();
		ArrayList<ARPackage> arPacks = new ArrayList<>();
		while(data_iterator.hasNext()) {
			HashMap<String,String> eHashMap = data_iterator.next();
			ARPackage ar = new ARPackage();
			ar.setLatitude(Float.valueOf(eHashMap.get("latitude")));
			ar.setLongitude(Float.valueOf(eHashMap.get("longitude")));
			ar.setContent(eHashMap.get("content"));
			arPacks.add(ar);
		}
		
		ARPackageManager arPackageManager = new ARPackageManager();
		if(arPackageManager.installPacks(roomUid, arPacks)) {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(0, "success."));
			return;
		}
		else {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90013, "AR packs info contain illegal parameters."));
			return;
		}
	}

}
