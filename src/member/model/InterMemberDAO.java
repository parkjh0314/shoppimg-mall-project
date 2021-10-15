package member.model;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface InterMemberDAO {

	// 회원가입을 해주는 메소드 (tbl_member 테이블에 insert)
	int registerMember(MemberVO member) throws SQLException;
	
	// ID 중복검사(tbl_member 테이블에서 userid 가 존재하면 true를 리턴해주고, userid가 존재하지 않으면 false를 리턴한다.)
	boolean idDuplicateCheck(String userid) throws SQLException; 

	// ID 중복검사(tbl_member 테이블에서 email이 존재하면 true를 리턴해주고, email이 존재하지 않으면 false를 리턴한다.)
	boolean emailDuplicateCheck(String email) throws SQLException;

	// 입력받은 paraMap을 가지고 한명의 회원정보를 리턴시켜주는 메소드(로그인 처리)
	MemberVO selectOneMember(Map<String, String> paraMap) throws SQLException;

	// 아이디 찾기 (성명, 이메일을 입력받아서 해당 사용자의 아이디를 알려준다.)
	String findUserid(Map<String, String> paraMap) throws SQLException;

	// 비밀번호 찾기 (성명, 이메일을 입력받아서 해당 사용자가 존재하는지 유무를 알려준다.)
	boolean isUserExist(Map<String, String> paraMap) throws SQLException;

	// 비밀번호 변경하기
	int pwdUpdate(Map<String, String> paraMap) throws SQLException;

	// 회원의 coin 변경하기
	int coinUpdate(Map<String, String> paraMap) throws SQLException;

	// 회원의 개인정보 변경하기
	int updateMember(MemberVO member) throws SQLException;

	// 페이징 처리를 하지않고 모든 회원목록 또는 검색조건에 해당하는 회원목록 보여주기
	List<MemberVO> selectMember(Map<String,String> paraMap) throws SQLException;

	//페이징 처리를 하여 모든 회원목록 또는 검색조건에 해당하는 회원목록 보여주기 
	List<MemberVO> selectPagingMember(Map<String, String> paraMap) throws SQLException;

	//페이징처리를 위해 전체회원에 대한 총 페이지개수 알아오기(select)
	int getTotalPage(Map<String, String> paraMap) throws SQLException;

	// userid 값을 입력받아서 회원 1명에 대한 상세정보를 알아오기(select)
	MemberVO memberOneDetail(String userid) throws SQLException;
	
	
}
