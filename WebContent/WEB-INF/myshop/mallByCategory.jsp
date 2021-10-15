<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:include page="../header.jsp"/>

<style>
	.moreProdInfo {
		display: inline-block;
		margin: 10px;
	}
	
	div.moreProdInfo > ul {
		text-align: left;
	}
	
	div.morProdInfo label.prodInfo {
		display: inline-block;
		width: 70px;
		/* border: solid 1px gray; */
	}
	
</style>

<script type="text/javascript">
	
	$(document).ready(function(){
		
	});

</script>


	<%-- === 특정 카테고리에 속하는 제품들을 일반적인 페이징 처리하여 조회(select)해온 결과 === --%>
	<div>
		<c:forEach begin="1" end="1" var="pvo" items="${productList}">
			<div style="margin:20px 0; font-size:20pt"> - ${pvo.categvo.cname} - </div>
		</c:forEach>
	 
	  <c:forEach var="pvo" items="${productList}" varStatus="status">
         <div class='moreProdInfo'>
              <ul style='list-style-type: none; border: solid 0px red; padding :0px; width :240px;'>
                  <li><a href='/MyMVC/shop/prodView.up?pnum=${pvo.pnum}'><img width='120px;' height='130px' src='/MyMVC/images/${pvo.pimage1}'/></a></li>
                   <li><label class='prodInfo'>스펙명</label>${pvo.spvo.sname}</li>
                   <li><label class='prodInfo'>제품명</label>${pvo.pname}</li>
                   <li><label class='prodInfo'>정가</label><span style="color: red; text-decoration: line-through;"><fmt:formatNumber value="${pvo.price}" pattern="#,###" /> 원</span></li> 
                   <li><label class='prodInfo'>판매가</label><span style="color: red; font-weight: bold;"><fmt:formatNumber value="${pvo.saleprice}" pattern="#,###" /> 원</span></li>
                   <li><label class='prodInfo'>할인율</label><span style="color: blue; font-weight: bold;">[${pvo.discountPercent}] 할인</span></li>
                   <li><label class='prodInfo'>포인트</label><span style="color: orange;">${pvo.point} POINT</span></li>
               </ul>
           </div>
           <c:if test="${status.count%5 == 0}">
           <br>
           </c:if>
        </c:forEach>
      
      	<div>${pageBar}</div>
      </div>