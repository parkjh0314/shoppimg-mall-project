package myshop.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import common.controller.AbstractController;
import myshop.model.InterProductDAO;
import myshop.model.ProductDAO;

public class LikeDislikeCountAction extends AbstractController {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String pnum = request.getParameter("pnum");
		
		InterProductDAO pdao = new ProductDAO();
		
		Map<String, Integer> map = pdao.getLikeDislikeCnt(pnum);
		// 테이블에서 select만 해서 가져올 것이므로 따로 VO를 만들지 않고 Map에 담아 보낸다.
		
		JSONObject jsonObj = new JSONObject(); //json형태로 보내주기 위한 JSON객체 {}
		
		jsonObj.put("likecnt", map.get("likecnt"));			// {"likecnt":1}
		jsonObj.put("dislikecnt", map.get("dislikecnt"));	// {"likecnt":1, "dislikecnt":0}
		
		String json = jsonObj.toString(); // "{"likecnt":1, "dislikecnt":0}"
		
		request.setAttribute("json", json);
		
		//super.setRedirect(false);
		super.setViewPage("/WEB-INF/jsonview.jsp");
	}

}
