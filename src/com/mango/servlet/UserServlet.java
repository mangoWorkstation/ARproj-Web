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

import com.mango.aliyun.AliSmsSender;
import com.mango.entity.Record;
import com.mango.entity.User;
import com.mango.entityManager.RecordManager;
import com.mango.entityManager.UserManager;
import com.mango.utils.AuthCodeCreator;
import com.mango.utils.JsonDecodeFormatter;
import com.mango.utils.JsonEncodeFormatter;
import com.mango.utils.POST2String;

/**
 * Servlet implementation class UserServlet
 */
public class UserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String rex = "^1([358][0-9]|4[579]|66|7[0135678]|9[89])[0-9]{8}$"; 

       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendRedirect("http://120.78.177.77/error.html");
		return;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
			if ("10015".compareTo(code)==0) {
				getUserInfo(data, response, userManager);
				return;
			}
			else if("10018".compareTo(code)==0) {
				verifyOldTel(data, response, userManager);
			}
			else if("10019".compareTo(code)==0) {
				verifyNewTel(data, response, userManager);
			}
			else if("10020".compareTo(code)==0) {
				resetPwd(data, response, userManager);
			}
			else if("10021".compareTo(code)==0) {
				getRecords(data, response);
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
	
	private void getUserInfo(HashMap<String, String> data,HttpServletResponse response,UserManager userManager) throws IOException {
		String token = data.get("token");
		String uuid = data.get("uuid");
		
		User user = new User();
		//查询用户本人的基本信息
		if(uuid==null) {
			user = userManager.getBasicProfile(token);
		}
		//查询单个其他用户的基本信息
		else {
			user = userManager.getBasicProfile(uuid);
		}
		
		if(user!=null) {
			response.getWriter().write(JsonEncodeFormatter.parser(0, user.toSecureHashMap()));
			return;
		}
		else {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90015, "Invalid uuid."));
			return;
		}
	}
	
	private void verifyOldTel(HashMap<String, String> data,HttpServletResponse response,UserManager userManager) throws IOException{
		String token = data.get("token");
		String authCode = data.get("authCode");
		String newTel = data.get("newTel");
		if(newTel.matches(UserServlet.rex)==false) {
			try {
				response.getWriter().write(JsonEncodeFormatter.universalResponse(90002, "Illegal Phone Number."));
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;	
		}
		
		if(userManager.verifyAuthCode(authCode, token)) {
			String newAuthCode = AuthCodeCreator.create();
			if(userManager.updateAuthCode(newAuthCode, token)) {
				if(AliSmsSender.sendAuthCodeSms(newTel, newAuthCode)) {
					response.getWriter().write(JsonEncodeFormatter.universalResponse(0, "AuthCode OK.Notify client for new SMS."));
					return;
				}
				else {
					response.getWriter().write(JsonEncodeFormatter.universalResponse(90010, "Server Busy."));
					return;
				}
			}
		}
		else {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90004, "Wrong Auth Code."));
			return;
		}
	}
	
	private void verifyNewTel(HashMap<String, String> data,HttpServletResponse response,UserManager userManager) throws IOException{
		String token = data.get("token");
		String authCode = data.get("authCode");
		String newTel = data.get("newTel");
		if(newTel.matches(UserServlet.rex)==false) {
			try {
				response.getWriter().write(JsonEncodeFormatter.universalResponse(90002, "Illegal Phone Number."));
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;	
		}
		
		if(userManager.getBasicProfile(newTel)!=null) {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90006, "Illegal Parameters or existing tel."));
			return;

		}
		
		//此处不把验证码字段还原为空，让数据库自动清除
		if(userManager.verifyAuthCode(authCode, token)&&userManager.updateTel(token, newTel)) {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(0, "New Tel updated."));
			return;
		}
		else {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90004, "Wrong Auth Code."));
			return;
		}
	}
	
	private void resetPwd(HashMap<String, String> data,HttpServletResponse response,UserManager userManager) throws IOException{
		String token = data.get("token");
		String authCode = data.get("authCode");
		String newSHAPwd = data.get("newSHAPwd");
		
		if(newSHAPwd.length()!=128) {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90006, "Illegal parameters.Check Again."));
			return;
		}
		
		if(userManager.verifyAuthCode(authCode, token)) {
			if(userManager.resetPassword(newSHAPwd, token)) {
				response.getWriter().write(JsonEncodeFormatter.universalResponse(0, "Password reset"));
				return;
			}
		}
		else {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90004, "Wrong Auth Code."));
			return;
		}
	}
	
	@SuppressWarnings("unchecked")
	private void getRecords(HashMap<String, String> data,HttpServletResponse response) throws IOException{
		String uuid = data.get("uuid");
		String from_t = data.get("from_t");
		String to_t = data.get("to_t");
		String limit = data.get("limit");
		
		RecordManager recordManager = new RecordManager();
		
		
		ArrayList<Record> records = null;
		if(from_t!=null&&to_t!=null&&limit!=null) {
			if(Long.valueOf(from_t)>Long.valueOf(to_t) || Integer.valueOf(limit)>100) {
				response.getWriter().write(JsonEncodeFormatter.universalResponse(90006, "Illegal parameters.Check Again."));
				return;
			}
			records = recordManager.getByTime(from_t, to_t, Integer.valueOf(limit), uuid);
		}
		else {
			records = recordManager.getByUser(uuid);
		}
		
		if(records.size()>0) {
			ArrayList<Map<String, String>> record_parsed = new ArrayList<>();
			Iterator<Record> recordIterator = records.iterator();
			while(recordIterator.hasNext()) {
				record_parsed.add(recordIterator.next().toHashMap());
			}
			response.getWriter().write(JsonEncodeFormatter.parser(0, record_parsed));
			return;
		}
		else {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90016, "No records yet."));
			return;
		}
	}

}
