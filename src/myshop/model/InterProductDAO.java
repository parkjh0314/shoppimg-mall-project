package myshop.model;

import java.sql.SQLException;
import java.util.*;

public interface InterProductDAO {

	// 메인페이지에 보여지는 상품이미지파일명을 모두 조회(select)하는 메소드
	// DTO(Data Transfer Object) == VO(Value Object)
	List<ImageVO> imageSelectAll() throws SQLException;
	
	// Ajax(JSON)를 사용하여 상품목록을 "더보기" 방식으로 페이징처리해주기 위해 스펙별로 제품의 전체개수 알아오기  
	int totalPspecCount(String fk_snum) throws SQLException;

	// Ajax(JSON)를 이용한 더보기 방식(페이징처리)으로 상품정보를 8개씩 잘라서(start ~ end) 조회해오기
	List<ProductVO> selectBySpecName(Map<String, String> paraMap) throws SQLException;

	// tbl_category 테이블에서 카테고리 대분류 번호(cnum), 카테고리코드(code), 카테고리명(cname)을 조회해오기 
	// VO 를 사용하지 않고 Map 으로 처리해보겠습니다.
	List<HashMap<String, String>> getCategoryList() throws SQLException;

	// spec 목록을 보여주고자 한다.
	List<SpecVO> selectSpecList() throws SQLException;

	// 제품번호 채번해오기
	int getPnumOfProduct() throws SQLException;

	// tbl_product 테이블에 제품정보 insert 하기
	int productInsert(ProductVO pvo) throws SQLException;

	// tbl_product_imagefile 테이블에 제품의 추가이미지 파일명 insert 해주기 
	int product_imagefile_Insert(int pnum, String attachFileName) throws SQLException;

	// 제품번호를 가지고서 해당 제품의 정보를 조회해오기
	ProductVO selectOneProductByPnum(String pnum) throws SQLException;

	// 제품번호를 가지고서 해당 제품의 추가된 이미지 정보를 조회해오기
	List<String> getImagesByPnum(String pnum) throws SQLException;

	// Ajax를 이용한 특정 제품의 상품후기를 입력(insert)하기
	int addComment(PurchaseReviewsVO reviewsvo) throws SQLException;

	// Ajax를 이용한 특정 제품의 상품후기를 조회(select)하기
	List<PurchaseReviewsVO> commentList(String fk_pnum) throws SQLException;
	
	// 특정 회원이 특정 제품에 대해 좋아요에 투표하기(insert)
	int likeAdd(Map<String, String> paraMap) throws SQLException;

	// 특정 회원이 특정 제품의 싫어요에 투표하기(insert)
	int dislikeAdd(Map<String, String> paraMap) throws SQLException;

	// 특정 제품에 대해 좋아요 개수와 싫어요 개수를 가져오기
	Map<String, Integer> getLikeDislikeCnt(String pnum) throws SQLException;

	// 특정 카테고리에 속하는 제품들을 일반적인 페이징 처리하여 조회(select)해오기
	List<ProductVO> selectProductBycategory(Map<String, String> paraMap) throws SQLException;

	// 페이지바를 만들기 위해서 특정카테고리의 제품개수에 대한 총페이지수 알아오기(select)
	int getTotalPage(String cnum) throws SQLException;
	
	
	
	
}
