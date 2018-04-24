package com.mango.servlet;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.mango.aliyun.AliSmsSender;
import com.mango.entity.User;
import com.mango.entityManager.UserManager;
import com.mango.utils.AuthCodeCreator;
import com.mango.utils.JsonDecodeFormatter;
import com.mango.utils.JsonEncodeFormatter;
import com.mango.utils.POST2String;


/**
 * Servlet implementation class AuthCodeServlet
 */
public class AuthCodeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String rex = "^1([358][0-9]|4[579]|66|7[0135678]|9[89])[0-9]{8}$"; 
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AuthCodeServlet() {
        super();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    		resp.sendRedirect("http://120.78.177.77/error.html");
    	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Content-Type", "application/json;charset=utf8");
		
		//获取POST报文，并转换为字符串
		String rootStr = POST2String.convert(request.getInputStream());
		System.out.println(rootStr);
		
		//解析JSON字符串
		HashMap<String, Object> params = JsonDecodeFormatter.decodeDataObject(rootStr);
		String code = (String)params.get("code");
		
		//校验请求类型，不满足则拒绝，测试通过2018-03-27
		if("10001".compareTo(code)==0) {
			requestRegisterAuthCode(params, response);
		}
		else if ("10002".compareTo(code)==0) {
			verifyRegister(params, response);
		}
		else if("10003".compareTo(code)==0) {
			forcedPresetPassword(params, response);
		}
		else if("10004".compareTo(code)==0) {
			updateUserProfile(params, response);
		}
		else if("10017".compareTo(code)==0) {
			verifyCurrentUser(params, response);
		}
		else {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90001, "Invalid Request Code."));
			return;	
		}
		
		
	}
	
//	//测试阶段，暂时取消,默认返回成功
//	private boolean sendAuthCodeSms(String tel,String authCode) {
//		try {
//			SendSmsResponse response = AliSmsSender.sendSms(tel, authCode);
//			String result = response.getCode();
////			String result = "OK";
//			if("OK".compareTo(result)==0) {
//				return true;
//			}
//			else {
//				return false;
//			}
//		} catch (ClientException e) {
//			e.printStackTrace();
//		}
//		return true;
//	}
	
	@SuppressWarnings("unchecked")
	private void verifyRegister(HashMap<String, Object> params , HttpServletResponse response) {
		HashMap<String, String> data = (HashMap<String, String>)params.get("data");
		String tel = data.get("tel");
		String authCode = data.get("authCode");
		UserManager userManager = new UserManager();
		if(userManager.verifyRegister(tel, authCode)) {
			try {
				response.getWriter().write(JsonEncodeFormatter.universalResponse(0, "success"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		else {
			try {
				response.getWriter().write(JsonEncodeFormatter.universalResponse(90004, "Wrong Auth Code.Register Failed."));
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void requestRegisterAuthCode(HashMap<String, Object> params, HttpServletResponse response) {
		HashMap<String, String> data = (HashMap<String, String>)params.get("data");
		String tel = data.get("tel");
		//强正则表达式进行校验,不满足的将拒绝，测试通过2018-03-27
		if(tel.matches(AuthCodeServlet.rex)==false) {
			try {
				response.getWriter().write(JsonEncodeFormatter.universalResponse(90002, "Illegal Phone Number."));
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;	
		}
		
		String authCode = AuthCodeCreator.create();
		if(AliSmsSender.sendAuthCodeSms(tel, authCode)) {
			try {
				UserManager userManager = new UserManager();
				if(userManager.registerNewUser(tel, authCode)) {
					response.getWriter().write(JsonEncodeFormatter.universalResponse(0, "success"));
				}
				else {
					response.getWriter().write(JsonEncodeFormatter.universalResponse(90003, "Phone Number Existed OR AuthCode NOT Expired! Tell client input auth code again."));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void forcedPresetPassword(HashMap<String, Object> params, HttpServletResponse response) {
		HashMap<String, String> data = (HashMap<String, String>)params.get("data");
		String tel = data.get("tel");
		String pwdSHA512 = data.get("SHApwd");
		UserManager userManager = new UserManager();
		String newToken = userManager.forcedPresetPassword(pwdSHA512, tel);
		if(newToken!=null) {
			try {
				HashMap<String, String> eMap = new HashMap<>();
				eMap.put("token", newToken);
				eMap.put("timestamp",String.valueOf(System.currentTimeMillis()));
				response.getWriter().write(JsonEncodeFormatter.parser(0, eMap));
				return;
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		else {
			try {
				response.getWriter().write(JsonEncodeFormatter.universalResponse(90005, "Not New User.Preset Password NOT Allowed."));
				return;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void updateUserProfile(HashMap<String, Object> params, HttpServletResponse response) throws IOException {
		HashMap<String, String> data = (HashMap<String, String>)params.get("data");
		String token = data.get("token");
		User user = new User();
		user.setName(data.get("name"));
		user.setGender(Integer.valueOf(data.get("gender")));
		user.setAge(Integer.valueOf(data.get("age")));
		user.setWeight(Float.valueOf(data.get("weight")));
		user.setHeight(Float.valueOf(data.get("height")));
		user.setProvince(data.get("province"));
		user.setCity(data.get("city"));
		UserManager userManager = new UserManager();
		if(userManager.updateUserProfile(user, token)) {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(0, "success"));
			return;
		}
		else {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90006, "Illegal parameters.Check Again."));
		}
	}
	
	@SuppressWarnings("unchecked")
	private void verifyCurrentUser(HashMap<String, Object> params,HttpServletResponse response) throws IOException{
		HashMap<String, String> data = (HashMap<String, String>)params.get("data");
		String token = data.get("token");
		UserManager userManager = new UserManager();
		if(userManager.checkToken(token)) {
			String authCode = AuthCodeCreator.create();
			if(userManager.updateAuthCode(authCode, token)) {
				response.getWriter().write(JsonEncodeFormatter.universalResponse(0, "SMS sent."));
				return;
			}
		}
		else {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90009, "Token Invalid or expired."));
			return;
		}
	}

}
