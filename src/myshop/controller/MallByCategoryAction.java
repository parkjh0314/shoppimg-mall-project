package myshop.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import common.controller.AbstractController;
import myshop.model.InterProductDAO;
import myshop.model.ProductDAO;
import myshop.model.ProductVO;

public class MallByCategoryAction extends AbstractController {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// 카테고리 목록을 조회해오기
		super.getCategoryList(request);
		
		// 로그인을 하면 시작페이지로 가는 것이 아니라 방금 보았던 그 페이지로 그대로 가기 위함
		super.goBackRUL(request);
		
		String cnum = request.getParameter("cnum"); //카테고리 번호
		
		// *** 카테고리번호에 해당하는 제품들을 페이징 처리하여 보여주기 *** //
		String currentShowPageNo = request.getParameter("currentShowPageNo");
		// currentShowPageNo 은 사용자가 보고자하는 페이지바의 페이지번호 이다.
	    // 카테고리 메뉴에서 카테고리명만을 클릭했을 경우에는 currentShowPageNo 은 null 이 된다.
	    // currentShowPageNo 이 null 이라면 currentShowPageNo 을 1 페이지로 바꾸어야 한다.
		
		if(currentShowPageNo == null) {
			currentShowPageNo ="1";
		}
		
		// 한 페이지당 화면상에 보여줄 제품의 개수는 10 으로 한다. sizePerPage 는 ProductDAO 에서 상수로 설정해 두었음.
		
		// === GET 방식이므로 사용자가 웹브라우저 주소창에서 currentShowPageNo 에 숫자 아닌 문자를 입력한 경우 또는 
	    //     int 범위를 초과한 숫자를 입력한 경우라면 currentShowPageNo 는 1 페이지로 만들도록 한다. ==== //
		try {
			Integer.parseInt(currentShowPageNo);
		}catch (NumberFormatException e) {
			currentShowPageNo = "1";
		}
		
		InterProductDAO pdao = new ProductDAO();
		
		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("cnum", cnum);
		paraMap.put("currentShowPageNo", currentShowPageNo);
		
		// 특정 카테고리에 속하는 제품들을 일반적인 페이징 처리하여 조회(select)해오기
		List<ProductVO> productList = pdao.selectProductBycategory(paraMap);
		request.setAttribute("productList", productList);
		
		// **** ========= 페이지바 만들기 ========= **** //
	      /*
	          1개 블럭당 10개씩 잘라서 페이지 만든다.
	          1개 페이지당  10개행을 보여준다라면 총 몇개 블럭이 나와야 할까? 
	             총 제품의 개수가 423개 이고, 1개 페이지당 보여줄 제품의 개수가 10개 이라면 
	          412/10 = 41.2 ==> 42(totalPage)        
	              
	          1블럭               1 2 3 4 5 6 7 8 9 10 [다음]
	          2블럭   [이전] 11 12 13 14 15 16 17 18 19 20 [다음]
	          3블럭   [이전] 21 22 23 24 25 26 27 28 29 30 [다음]
	          4블럭   [이전] 31 32 33 34 35 36 37 38 39 40 [다음]
	          5블럭   [이전] 41 42 
	       */
		
		// 페이지바를 만들기 위해서 특정카테고리의 제품개수에 대한 총페이지수 알아오기(select)
		int totalPage = pdao.getTotalPage(cnum);
		
	//   System.out.println("~~~ 확인용 totalPage => " + totalPage);
	      
	      // ==== !!! 공식 !!! ==== // 
	   /*
	       1  2  3  4  5  6  7  8  9  10  -- 첫번째 블럭의 페이지번호 시작값(pageNo)은  1 이다.
	       11 12 13 14 15 16 17 18 19 20  -- 두번째 블럭의 페이지번호 시작값(pageNo)은 11 이다.   
	       21 22 23 24 25 26 27 28 29 30  -- 세번째 블럭의 페이지번호 시작값(pageNo)은 21 이다.
	       
	       currentShowPageNo      pageNo
	       --------------------------------------------------------------------------------------
	            1                   1  = ( (currentShowPageNo - 1)/blockSize ) * blockSize + 1 
	            2                   1  = ( (2 - 1)/10 ) * 10 + 1
	            3                   1  = ( (3 - 1)/10 ) * 10 + 1
	            4                   1  = ( (4 - 1)/10 ) * 10 + 1
	            5                   1  = ( (5 - 1)/10 ) * 10 + 1
	            6                   1  = ( (6 - 1)/10 ) * 10 + 1
	            7                   1  = ( (7 - 1)/10 ) * 10 + 1
	            8                   1  = ( (8 - 1)/10 ) * 10 + 1
	            9                   1  = ( (9 - 1)/10 ) * 10 + 1
	            10                  1  = ( (10 - 1)/10 ) * 10 + 1
	            
	            11                 11  = ( (11 - 1)/10 ) * 10 + 1
	            12                 11  = ( (12 - 1)/10 ) * 10 + 1 
	            13                 11  = ( (13 - 1)/10 ) * 10 + 1
	            14                 11  = ( (14 - 1)/10 ) * 10 + 1
	            15                 11  = ( (15 - 1)/10 ) * 10 + 1
	            16                 11  = ( (16 - 1)/10 ) * 10 + 1
	            17                 11  = ( (17 - 1)/10 ) * 10 + 1
	            18                 11  = ( (18 - 1)/10 ) * 10 + 1
	            19                 11  = ( (19 - 1)/10 ) * 10 + 1
	            20                 11  = ( (20 - 1)/10 ) * 10 + 1  
	            
	            21                 21  = ( (21 - 1)/10 ) * 10 + 1 
	            22                 21  = ( (22 - 1)/10 ) * 10 + 1 
	            23                 21  = ( (23 - 1)/10 ) * 10 + 1 
	            ..                 21  = ( (.. - 1)/10 ) * 10 + 1 
	            29                 21  = ( (29 - 1)/10 ) * 10 + 1 
	            30                 21  = ( (30 - 1)/10 ) * 10 + 1 
	    */
		
		String pageBar = "";
		
		int blockSize = 10;
		// blockSize는 블럭(토막)당 보여지는 페이지번호의 개수이다.
		
		int loop = 1;
		// loop는 1부터 증가하여 1개 블럭을 이루는 페이지번호의 개수(지금은 10개)까지만 증가하는 용도이다.
		
		int pageNo = 0;
		// pageNo는 페이지 바에서 보여지는 첫번째 번호이다.
		
		// !!!!! 다음은 pageNo를 구하는 공식이다. !!!!! //
		pageNo = ((Integer.parseInt(currentShowPageNo) - 1) / blockSize) * blockSize +1;
		
		// ****[맨처음][이전] 만들기 **** // 
		// pageNo - 1 == 11  - 1 == 10> currentShowPageNo
		if(pageNo != 1) {
			pageBar += "&nbsp;<a href='mallByCategory.up?currentShowPageNo=1&cnum="+cnum+"'[맨처음]</a>&nbsp;";
			pageBar += "&nbsp;<a href='mallByCategory.up?currentShowPageNo=1"+(pageNo-1)+"&cnum="+cnum+"'[이전]</a>&nbsp;";
		}
		
		while( !(loop > blockSize || pageNo > totalPage ) ) {
	         
	         if( pageNo == Integer.parseInt(currentShowPageNo) ) {
	            pageBar += "&nbsp;<span style='border:solid 1px gray; color:red; padding: 2px 4px;'>"+pageNo+"</span>&nbsp;";  
	         }
	         else {
	            pageBar += "&nbsp;<a href='mallByCategory.up?currentShowPageNo="+pageNo+"&cnum="+cnum+"'>"+pageNo+"</a>&nbsp;"; 
	         }
	         
	         loop++;   // 1 2 3 4 5 6 7 8 9 10 
	                   
	         pageNo++; //  1  2  3  4  5  6  7  8  9 10 
	                   // 11 12 13 14 15 16 17 18 19 20 
	                   // 21 
	      }// end of while---------------------------------
		
		// **** [다음][마지막] 만들기 **** //
	      // pageNo ==> 11
	      if( !( pageNo > totalPage ) ) {
	         pageBar += "&nbsp;<a href='mallByCategory.up?currentShowPageNo="+pageNo+"&cnum="+cnum+"'>[다음]</a>&nbsp;";
	         pageBar += "&nbsp;<a href='mallByCategory.up?currentShowPageNo="+totalPage+"&cnum="+cnum+"'>[마지막]</a>&nbsp;";  
	      }
		
	      request.setAttribute("pageBar", pageBar);
	      
	      super.setRedirect(false);
	      super.setViewPage("/WEB-INF/myshop/mallByCategory.jsp");
	      
	}

}
