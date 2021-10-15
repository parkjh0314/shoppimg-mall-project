package common.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainController extends AbstractController {

	@Override
	public String toString() { //Object클래스의 메소드 재정의
		return "### 클래스 MainController의 인스턴스 메소드 toString() 호출함 ###";
	}
	
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//System.out.println("### 확인용 MainController의 인스턴스 메소드 excute가 호출됨 ###");
		
		super.setRedirect(true);
		super.setViewPage("index.up");  //	/MyMVC/index.up 페이지로 이동한다. 
		
		
	}

}
