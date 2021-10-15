package common.controller;

import java.sql.SQLException;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import member.model.MemberVO;
import my.util.MyUtil;
import myshop.model.InterProductDAO;
import myshop.model.ProductDAO;

public abstract class AbstractController implements InterCommand {

/*
    === 다음 내용은 우리끼리의 약속이다. ===

    ※ view 단 페이지(.jsp)로 이동시 forward 방법(dispatcher)(데이터까지 넘김, url은 그대로)으로 이동시키고자 한다라면 
       자식클래스에서는 부모클래스에서 생성해둔 메소드 호출시 아래와 같이 하면 되게끔 한다.
     
    super.setRedirect(false); 
    super.setViewPage("/WEB-INF/index.jsp");
    
    
          ※ URL 주소를 변경하여 페이지 이동시키고자 한다라면
          즉, sendRedirect(데이터 안넘기고 페이지만 바뀜, url도 바뀜) 를 하고자 한다라면    
          자식클래스에서는 부모클래스에서 생성해둔 메소드 호출시 아래와 같이 하면 되게끔 한다.
          
    super.setRedirect(true);
    super.setViewPage("registerMember.up");               
*/
	
	private boolean isRedirect = false; 	//isRedirect 변수의 값이 false일 경우 -> view단 페이지(.jsp)로 이동하는 방법: forward 방식(더 많이 쓰임)
											//isRedirect 변수의 값이 true일 경우 -> view단 페이지(.jsp)로 이동하는 방법: sendRedirect 방식
	
	private String viewPage;				//viewPage는 isRedirect = false일 경우에는 -> view단 페이지(.jsp)의 경로명으로 정한다.
											//viewPage는 isRedirect = true일 경우에는 -> 이동해야할 페이지 URL주소로 정한다.
	
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public boolean isRedirect() {
		return isRedirect;
	}

	public void setRedirect(boolean isRedirect) {
		this.isRedirect = isRedirect;
	}

	public String getViewPage() {
		return viewPage;
	}

	public void setViewPage(String viewPage) {
		this.viewPage = viewPage;
	}
	
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	// 로그인 유무를 검사해서 로그인 했으면 true를 리턴해주고 로그인을 안했으면 false를 리턴해주도록 한다.
	public boolean checkLogin(HttpServletRequest request) {
		
		HttpSession session = request.getSession(); //세션불러오기
		MemberVO loginuser = (MemberVO) session.getAttribute("loginuser"); // 세션에 저장된 value값을 key값을 넣어 가져오기
		
		if(loginuser != null) {
			//로그인 한 경우
			return true;
		}
		else {
			//로그인 안한 경우
			return false;
		}
	}
	
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// *** 제품목록(Category)을 보여줄 메소드 생성하기 *** //
	// VO를 사용하지 않고 Map으로 처리해보겠다.
	public void getCategoryList(HttpServletRequest request) throws SQLException {
		
		InterProductDAO pdao = new ProductDAO();
		List<HashMap<String, String>> categoryList = pdao.getCategoryList();
	
		request.setAttribute("categoryList", categoryList);

		
	}
	
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	// 로그인을 하면 시작페이지로 가지 않고 방금 보았던 그 페이지에 그대로 돌아가기 위함
	public void goBackRUL(HttpServletRequest request) {
		HttpSession session = request.getSession();
		session.setAttribute("goBackURL", MyUtil.getCurrentURL(request));
	}
	
}
