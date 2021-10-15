package common.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import myshop.model.ImageVO;
import myshop.model.InterProductDAO;
import myshop.model.ProductDAO;

public class IndexController extends AbstractController {

	@Override
	public String toString() { //Object클래스의 메소드 재정의
		return "@@@ 클래스 IndexController의 인스턴스 메소드 toString() 호출함 @@@";
	}
	
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//System.out.println("@@@ 확인용 IndexController의 인스턴스 메소드 excute가 호출됨 @@@");
		
		InterProductDAO pdao = new ProductDAO();
		List<ImageVO> imgList = pdao.imageSelectAll();
		
		request.setAttribute("imgList", imgList);
		
		//super.setRedirect(false); 부모클래스에서 default가 false로 되어있음
		//this.setRedirect(false);
		//setRedirect(false); 세개 다 똑같
		
		super.setViewPage("/WEB-INF/index.jsp");
		
	}

}
