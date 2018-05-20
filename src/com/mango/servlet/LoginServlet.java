package com.mango.servlet;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mango.entityManager.UserManager;
import com.mango.utils.JsonDecodeFormatter;
import com.mango.utils.JsonEncodeFormatter;
import com.mango.utils.POST2String;

/**
 * Servlet implementation class RegisterServlet
 */
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String rex = "^1([358][0-9]|4[579]|66|7[0135678]|9[89])[0-9]{8}$"; 


    /**
     * Default constructor. 
     */
    public LoginServlet() {
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
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		response.setHeader("Content-Type", "application/json;charset=utf8");
		//获取POST报文，并转换为字符串
		String rootStr = POST2String.convert(request.getInputStream());
		System.out.println(rootStr);
		
		//解析JSON字符串
		HashMap<String, Object> params = JsonDecodeFormatter.decodeDataObject(rootStr);
		
		String code = (String) params.get("code");
		if("10005".compareTo(code)==0) {
			appKey_login(params, response);
			return;
		}
		else if("10006".compareTo(code)==0) {
			token_login(params, response);
			return;
		}
		else {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90001, "Invalid Request Code."));
			return;	
		}
	}
	
	@SuppressWarnings("unchecked")
	private void appKey_login(HashMap<String, Object> params , HttpServletResponse response) throws IOException {
		HashMap<String, String> data = (HashMap<String, String>)params.get("data");
		String tel = data.get("tel");
		String SHApwd = data.get("SHAPwd");
		String pushID = data.get("pushID");
		
		//强正则表达式进行校验,不满足的将拒绝，测试通过2018-03-27
		if(tel.matches(LoginServlet.rex)==false) {
			try {
				response.getWriter().write(JsonEncodeFormatter.universalResponse(90002, "Illegal Phone Number."));
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;	
		}
		
		if(pushID==null) {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90008, "Push ID Empty."));
			return;
		}
		UserManager userManager = new UserManager();
		HashMap<String, String> result = userManager.loginWithSHApwd(SHApwd, tel,pushID);
		if(result != null) {
			HashMap<String, String> datum = new HashMap<>();
			datum.put("token", result.get("token"));
			datum.put("uuid", result.get("uuid"));
			response.getWriter().write(JsonEncodeFormatter.parser(0, datum));
		}
		else {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90007, "Invalid tel or SHApwd."));
			return;
		}
	}
	
	@SuppressWarnings("unchecked")
	private void token_login(HashMap<String, Object> params , HttpServletResponse response) throws IOException{
		HashMap<String, String> data = (HashMap<String, String>)params.get("data");
		String token = data.get("token");
		String pushID = data.get("pushID");
		if(pushID==null) {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90008, "Push ID Empty."));
			return;
		}

		
		UserManager userManager = new UserManager();
		if(userManager.loginWithToken(token,pushID)) {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(0, "success"));
			return;
		}
		else {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90009, "Token Invalid or expired."));
			return;
		}
	}

}
