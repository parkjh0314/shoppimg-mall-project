package member.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import common.controller.AbstractController;

public class LogoutAction extends AbstractController {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 로그아웃 처리하기
		HttpSession session = request.getSession(); // 생성된 세션을 불러온다.
		
		// 첫번째 방법 : 세션을 그대로 존재하게끔 해두고 세션에 저장된 어떤 값을 삭제하기(지금은 로그인된 회원객체)
		session.removeAttribute("loginuser"); // 세션에서 key값이 loginuser인 value값을 삭제한다.
		
		// 두번째 방법 : 세션을 WAS 메모리상에서 아예 삭제해버리기
//		session.invalidate(); // 세션삭제

		super.setRedirect(true);
		super.setViewPage(request.getContextPath()+"/index.up");
	}

}
