package com.mango.servlet;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mango.aliyun.AliyunPushManager;
import com.mango.utils.JsonDecodeFormatter;
import com.mango.utils.JsonEncodeFormatter;
import com.mango.utils.POST2String;

/**
 * Servlet implementation class PushTestDemoSevlet
 */
public class TestDemoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TestDemoServlet() {
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
		
		if("11000".compareTo(code)==0) {
			testPushService(data, response);
			return;
		}
		else {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90001, "Invalid Request Code."));
			return;	
		}
	}
	
	private void testPushService(HashMap<String, String> data, HttpServletResponse response) {
		String deviceID = data.get("deviceID").trim();
		String title = data.get("title");
		String body = data.get("body");
		String type = data.get("type");
		try {
			AliyunPushManager aliyunPushManager = new AliyunPushManager();
			if("notice".compareTo(type)==0) {
				if (aliyunPushManager.pushNoticeToAndroid(title, body, deviceID)) {
					response.getWriter().write(JsonEncodeFormatter.universalResponse(0, "success"));
					return;
				}
				else {
					response.getWriter().write(JsonEncodeFormatter.universalResponse(90010, "Server Busy."));
					return;
				}
			}
			else if("msg".compareTo(type)==0) {
				if (aliyunPushManager.pushMessageToAndroid(title, body, deviceID)) {
					response.getWriter().write(JsonEncodeFormatter.universalResponse(0, "success"));
					return;
				}
				else {
					response.getWriter().write(JsonEncodeFormatter.universalResponse(90010, "Server Busy."));
					return;
				}
			}
			else {
				response.getWriter().write(JsonEncodeFormatter.universalResponse(90001, "Invalid Request Type."));
				return;
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

