package member.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import common.controller.AbstractController;
import member.model.InterMemberDAO;
import member.model.MemberDAO;
import member.model.MemberVO;

public class LoginAction extends AbstractController {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String method = request.getMethod(); //"GET" or "POST"
		
		if(!"POST".equalsIgnoreCase(method)) {
			//POST방식으로 넘어온 것이 아니라면
			String message = "비정상적인 경로로 들어왔습니다.";
			String loc = "javascript:history.back()";
			
			request.setAttribute("massage", message);
			request.setAttribute("loc", loc);
			
			// super.setRedirect(false);
			super.setViewPage("/WEB-INF/msg.jsp");
			
			return; // execute(HttpServletRequest request, HttpServletResponse response) 메소드 종료함
		}
		
		//POST방식으로 넘어온 것이라면
		String userid = request.getParameter("userid");
		String pwd = request.getParameter("pwd");
		
		// ===> 클라이언트의 ip주소를 알아오는 것 <=== //
		String clientip = request.getRemoteAddr();
		//참조 ==> D:\001지현\001 프로그래밍 공부\NCS\workspace(jsp)\MyMVC\WebContent\JSP 파일을 실행시켰을 때 IP 주소가 제대로 출력되기위한 방법.txt
		
		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("userid", userid);
		paraMap.put("pwd", pwd);
		paraMap.put("clientip", clientip);
		
		InterMemberDAO mdao = new MemberDAO();
		
		MemberVO loginuser = mdao.selectOneMember(paraMap);
		
		if(loginuser != null) { //로그인 성공시
			//System.out.println(">>> 확인용 로그인한 사용자명 : " + loginuser.getName());
		
						
			 if(loginuser.getIdle() == 1) { // 로그인한 회원의 idle이 1일때(휴면계정일때)
				 String message = "로그인을 한지 1년이 지나 휴면계정으로 전환되었습니다. 관리자에게 문의 바랍니다.";
				 String loc = request.getContextPath()+"/index.up";
				 // 원래는 위와 같이 index.up이 아니라 휴면계정을 풀어주는 페이지로 잡아주어야한다.
				 
				 request.setAttribute("message", message);
				 request.setAttribute("loc", loc);
				 
				 super.setRedirect(false);
				 super.setViewPage("/WEB-INF/msg.jsp");
				 
				 return;
			 }
			
			 	// !!!! session(세션)이라는 저장소에 로그인 되어진 loginuser을 저장시켜두어야 한다!!! //
				// session(세션) 이란? WAS 컴퓨터의 메모리 (RAM)의 일부분을 사용하는 것으로 접속한 클라이언트 컴퓨터에서 보내온 정보를 저장하는 용도로 쓰인다.
				// 클라이언트 컴퓨터가 WAS컴퓨터에 웹으로 접속을 하기만하면 무조건 자동적으로 WAS컴퓨터의 메모리(RAM)의 일부분에 session이 생성되어진다.
				// session은 클라이언트 컴퓨터 웹브라우저당 1개씩 생성되어진다.
				// 예를 들면 클라이언트 컴퓨터가 크롬웹브라우저로 WAS 컴퓨터에 웹으로 연결하면 새로운 session이 생성되고,
				// 또 이어서 동일한 클라이언트 컴퓨터가 엣지웹브라우저로 WAS컴퓨터에 웹으로 연결하면 또 하나의 새로운 session이 생성된다.
				/*
				  		 ---------------
				  		|  클라이언트A	|				 ---------------------------
				  		| A 웹브라우저	|				|							|
				  		 ---------------				|		  WAS 서버			|
														|							|
														|		RAM(A session)		|
			  		 	 ---------------				|	    RAM(B session)		|
				  		|  클라이언트B	|				|							|
				  		| B 웹브라우저	|				 ---------------------------
				  		 ---------------

					브라우저 하나당 세션하나씩 생성 됐다가 창끄면 세션도 지워짐
			  		세션(session)이라는 저장 영역에 loginuser 를 저장시켜두면 Command.properties 파일에 기술된 모든 클래스 및 모든 JSP 페이지(파일)에서 loginuser 정보를 사용할 수 있게 된다.
			  		그러므로 어떤 정보를 여러 클래스 또는 여러 jsp 페이지에서 공통적으로 사용하고자 한다면 세션(session)에 저장해야한다.
				 */
				
				 HttpSession session = request.getSession(); // 메모리에 생성되어 있는 session을 불러옴 // session은 우리가 만드는게 아니라 브라우저 켜면 자동 생성됨
				 
				 session.setAttribute("loginuser", loginuser);
				 // session(세션)에 로그인된 사용자 정보인 loginuser를 불러올때 사용할 키값:"loginuser"로 저장

			 
			 if(loginuser.isRequirePwdChange() == true) {
				 	String message = "비밀번호를 변경하신지 3개월이 지났습니다. 비밀번호를 변경하세요!!";
					String loc = request.getContextPath()+"/index.up";
					// 원래는 위와 같이 index.up이 아니라 사용자의 암호를 변경해주는 페이지로 잡아주어야한다.
					
					request.setAttribute("message", message);
					request.setAttribute("loc", loc);

					super.setRedirect(false);
					super.setViewPage("/WEB-INF/msg.jsp");
					
					return;
					
			 } else { // 휴면계정도 아니고 비밀번호 변경한지 3개월이 지나지도 않았을때(모든게 정상일때) 
				 // 바로 페이지 이동을 시킨다.
				 
				 // 특정 제품상세 페이지를 보았을 경우 로그인을 하면 home으로 가지 않고 바로전에 보고있던 제품상세페이지로 돌아가게해줌
				 String goBackURL = (String) session.getAttribute("goBackURL");
				 // /shop/prodView.up?pnum=36
				 // null

				 super.setRedirect(true);
				 
				 if(goBackURL != null) {
					 super.setViewPage(request.getContextPath()+"/"+goBackURL);
					 session.removeAttribute("goBackURL");
				 }
				 else { // 홈으로 돌아가줌
					 super.setViewPage(request.getContextPath()+"/index.up");
				 }
				 
			 }

			 
		} else { //로그인 실패시
			//System.out.println(">>> 확인용 로그인 실패!!! <<<");
			String message = "로그인 실패";
			String loc = "javascript:history.back()";
			
			request.setAttribute("message", message);
			request.setAttribute("loc", loc);

			super.setRedirect(false);
			super.setViewPage("/WEB-INF/msg.jsp");
			
		}
		
	}

}
