package member.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import common.controller.AbstractController;
import member.model.MemberVO;

public class CoinPurchaseTypeChoiceAction extends AbstractController {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// 코인충전을 하기위한 전제조건: 로그인이 되어있어야함
		
		if(super.checkLogin(request)) {
			// 로그인을 했으면
			
			String userid = request.getParameter("userid");
			
			HttpSession session = request.getSession();
			MemberVO loginuser = (MemberVO) session.getAttribute("loginuser");
			
			if(loginuser.getUserid().equals(userid)) { //로그인한 자신의 아이디와 넘어온 아이디 값이 동일한지 확인
				//로그인한 사용자가 자신의 코인을 충전하는 경우
				//super.setRedirect(false);
				super.setViewPage("/WEB-INF/member/coinPurchaseTypeChoice.jsp");
			}
			else {
				//로그인한 사용자가 다른사용자의 코인을 충전하려고 시도하는 경우
				String message = "다른사용자의 코인충전 시도는 불가합니다.";
				String loc = "javascript:history.back()";
				
				request.setAttribute("message", message);
				request.setAttribute("loc", loc);
				
				//super.setRedirect(false);
				super.setViewPage("/WEB-INF/msg.jsp");
				return;
			}
		}
		else {
			// 로그인을 안 했으면
			String message = "코인충전을 하기 위해서는 먼저 로그인을 하세요!!!";
			String loc = "javascript:history.back()";
			
			request.setAttribute("message", message);
			request.setAttribute("loc", loc);
			
			//super.setRedirect(false);
			super.setViewPage("/WEB-INF/msg.jsp");
		}
	}

}
