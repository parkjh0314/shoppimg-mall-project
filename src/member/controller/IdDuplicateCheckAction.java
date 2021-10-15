package member.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import common.controller.AbstractController;
import member.model.InterMemberDAO;
import member.model.MemberDAO;


public class IdDuplicateCheckAction extends AbstractController {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String userid = request.getParameter("userid");
		
		//System.out.println(">>> 확인용 userid : " + userid);
		
		InterMemberDAO mdao = new MemberDAO();
		boolean isExists = mdao.idDuplicateCheck(userid);
		
		JSONObject jsonObj = new JSONObject(); //{}
		jsonObj.put("isExists", isExists);	// {"isExists":true} 또는 {"isExists":false}로 만들어준다.
		
		String json = jsonObj.toString(); //{"isExists":true} 또는 {"isExists":false}이라는 문자열로 만들어준다.
		//System.out.println(">>> 확인용 json =>" + json );
		
		request.setAttribute("json", json);
		
		//super.setRedirect(false);
		super.setViewPage("/WEB-INF/jsonview.jsp");
	
		
	
	}

}
