package myshop.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.*;

import common.controller.AbstractController;
import myshop.model.*;

public class CommentListAction extends AbstractController {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String fk_pnum = request.getParameter("fk_pnum"); //제품번호
		
		InterProductDAO pdao = new ProductDAO();
		
		JSONArray jsArr = new JSONArray(); // [] ajax에 json배열의 형태로 돌려주기 위함
		
		List<PurchaseReviewsVO> commentList = pdao.commentList(fk_pnum); // 해당 제품번호에 해당하는 리뷰들을 PurchaseReviewsVO형태의 리스트로 받아옴
		
		if(commentList != null && commentList.size() > 0) { //commentList가 null도 아니고 리스트의 길이도 0보다 클 경우에
		
			for(PurchaseReviewsVO reviewsvo : commentList ) {
				JSONObject jsobj = new JSONObject();			  // {} {}
				jsobj.put("contents", reviewsvo.getContents());	  //{"contents":"제품후기내용"} {"contents":"제품후기내용2"}
				jsobj.put("name", reviewsvo.getMvo().getName());  //{"contents":"제품후기내용", "name":"작성자이름"} {"contents":"제품후기내용2", "name":"작성자이름2"}
				jsobj.put("writeDate", reviewsvo.getWriteDate()); //{"contents":"제품후기내용", "name":"작성자이름", "writeDate":"작성일자"} {"contents":"제품후기내용2", "name":"작성자이름2", "writeDate":"작성일자2"}
				
				jsArr.put(jsobj); // [] 생성해둔 JSONArray에 jsobj 객체를 넣어줌
 			}
		}
		
		String json = jsArr.toString(); // jsArr배열을 문자열 형태로 변환해줌.
		
		request.setAttribute("json", json);
		
		//super.setRedirect(false);
		super.setViewPage("/WEB-INF/jsonview.jsp");
		
		// [{},{},{}]
		
		
	}

}
