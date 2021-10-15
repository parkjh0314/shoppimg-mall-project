package member.model;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.sql.*;
import java.util.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import util.security.AES256;
import util.security.SecretMyKey;
import util.security.Sha256;

public class MemberDAO implements InterMemberDAO {

	private DataSource ds; // DataSource ds 는 아파치톰캣이 제공하는 DBCP(DB Connection Pool) 이다.
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;

	private AES256 aes;

	// 생성자
	public MemberDAO() {
		try {
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			ds = (DataSource) envContext.lookup("jdbc/mymvc_oracle");

			aes = new AES256(SecretMyKey.KEY); // SecretMyKey.KEY는 우리가 만든 비밀키이다.

			// etc.
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// 사용한 자원 반납 close() Method 생성하기

	private void close() {
		try {
			if (rs != null) {
				rs.close();
				rs = null;
			}
			if (pstmt != null) {
				pstmt.close();
				pstmt = null;
			}
			if (conn != null) {
				conn.close();
				conn = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	

	// 회원가입을 해주는 메소드 (tbl_member 테이블에 insert)

	@Override
	public int registerMember(MemberVO member) throws SQLException {

		int result = 0;

		try {

			conn = ds.getConnection();

			String sql = "insert into tbl_member(userid, pwd, name, email, mobile, postcode, address, detailaddress, extraaddress, gender, birthday) "
					+ "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, member.getUserid());
			pstmt.setString(2, Sha256.encrypt(member.getPwd())); // 암호를 SHA256 알고리즘으로 단방향 암호화 시킨다.
			pstmt.setString(3, member.getName());
			pstmt.setString(4, aes.encrypt(member.getEmail())); // 이메일을 AES256알고리즘으로 양방향 암호화 시킨다.
			pstmt.setString(5, aes.encrypt(member.getMobile())); // 휴대폰번호를 AES256알고리즘으로 양방향 암호화 시킨다.
			pstmt.setString(6, member.getPostcode());
			pstmt.setString(7, member.getAddress());
			pstmt.setString(8, member.getDetailaddress());
			pstmt.setString(9, member.getExtraaddress());
			pstmt.setString(10, member.getGender());
			pstmt.setString(11, member.getBirthday());

			result = pstmt.executeUpdate();

		} catch (GeneralSecurityException | UnsupportedEncodingException e) {
			e.printStackTrace();
		} finally {
			close();
		}

		return result;
	}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	

	// ID 중복검사(tbl_member 테이블에서 userid 가 존재하면 true를 리턴해주고, userid가 존재하지 않으면 false를
	// 리턴한다.)

	@Override
	public boolean idDuplicateCheck(String userid) throws SQLException {

		boolean isExist = false;

		try {

			conn = ds.getConnection();

			String sql = "select userid from tbl_member where userid = ?";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userid);

			rs = pstmt.executeQuery();

			isExist = rs.next(); // 행이 있으면(중복된 userid) true, 행이 없으면(사용가능한 userid) false;

		} finally {
			close();
		}

		return isExist;
	}

	@Override
	public boolean emailDuplicateCheck(String email) throws SQLException {

		boolean isExist = false;

		try {

			// db에 받아온 email값이 있는지 확인해보기

			conn = ds.getConnection();

			String sql = "select email from tbl_member where email = ?";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, email);

			rs = pstmt.executeQuery();

			isExist = rs.next(); // 행이 있으면(중복된 email) true, 행이 없으면(사용가능한 email) false;

		} finally {
			close();
		}

		return isExist;
	}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//입력받은 paraMap 을 가지고 한명의 회원정보를 리턴시켜주는 메소드(로그인 처리)
	@Override
	public MemberVO selectOneMember(Map<String, String> paraMap) throws SQLException {

		MemberVO member = null; // 아래의 if문 실행되지 않으면 null값을 리턴하고 끝남

		try {
			conn = ds.getConnection();

			String sql = "select userid, name, email, mobile, postcode, address, detailaddress, extraaddress, gender, birthyyyy, birthmm, birthdd "+
					"            , coin, point, registerday, pwdchangegap, lastlogingap, nvl(lastlogingap, trunc( MONTHS_BETWEEN(SYSDATE, registerday)) ) as lastlogingap "+
					"from "+
					"( "+
					"select userid, name, email, mobile, postcode, address, detailaddress, extraaddress, gender "+
					"    , substr(birthday,1,4) AS birthyyyy, substr(birthday,6,2) as birthmm, substr(birthday,9) as birthdd "+
					"    , coin, point, to_char(registerday, 'yyyy-mm-dd') as registerday "+
					"    , trunc( MONTHS_BETWEEN(SYSDATE, lastpwdchangedate) ) as pwdchangegap "+
					"from tbl_member "+
					"where status = 1 and userid = ? and pwd = ? "+
					") M "+
					"cross join "+
					"( "+
					"select trunc( MONTHS_BETWEEN(SYSDATE,  MAX(logindate)) ) as lastlogingap "+
					"from tbl_loginhistory "+
					"where fk_userid = ? "+
					") H";

			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, paraMap.get("userid"));
			pstmt.setString(2, Sha256.encrypt(paraMap.get("pwd")));
			pstmt.setString(3, paraMap.get("userid"));

			rs = pstmt.executeQuery();

			if (rs.next()) { //select 된 내용이 존재할 때
				member = new MemberVO();

				member.setUserid(rs.getString(1));
				member.setName(rs.getString(2));
				member.setEmail(aes.decrypt(rs.getString(3))); // 복호화
				member.setMobile(aes.decrypt(rs.getString(4))); // 복호화
				member.setPostcode(rs.getString(5));
				member.setAddress(rs.getString(6));
				member.setDetailaddress(rs.getString(7));
				member.setExtraaddress(rs.getString(8));
				member.setGender(rs.getString(9));
				member.setBirthday(rs.getString(10) + rs.getString(11) + rs.getString(12));
				member.setCoin(rs.getInt(13));
				member.setPoint(rs.getInt(14));
				member.setRegisterday(rs.getString(15));

				if (rs.getInt(16) >= 3) {
					// 마지막으로 암호를 변경한 날짜가 현재시각으로 부터 3개월이 지났으면 true
					// 마지막으로 암호를 변경한 날짜가 현재시각으로 부터 3개월이 지나지 않았으면 false
					member.setRequirePwdChange(true); // 로그인시 암호를 변경하라는 alert 를 띄우도록 한다.
				}

				if(rs.getInt(17) >= 12) {
					//마지막 로그인 한 날짜시간이 현재시각으로부터 1년이 지났으면 휴면으로 지정
					member.setIdle(1);
					
					// === tbl_member 테이블에 idle 컬럼의 값을 1(휴면중)으로 변경하기 === //
					sql = "update tbl_member set idle = 1 "
							+ "where userid = ? ";

					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, paraMap.get("userid"));

					pstmt.executeUpdate();
				}
				
				if(member.getIdle() != 1) { // 로그인 시도한 회원의 idle이 0이라면(휴면계정이 아닌경우)
				// === tbl_loginhistory(로그인기록) 테이블에 insert 하기 === //
				sql = " insert into tbl_loginhistory(fk_userid, clientip) " + " values(?, ?) ";

				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, paraMap.get("userid"));
				pstmt.setString(2, paraMap.get("clientip"));

				pstmt.executeUpdate();
				}
				
			}

		} catch (GeneralSecurityException | UnsupportedEncodingException e) {
			e.printStackTrace();
		} finally {
			close();
		}

		return member;
	}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String findUserid(Map<String, String> paraMap) throws SQLException {
		
		String userid = null;

		try {

			conn = ds.getConnection();

			String sql = "select userid from tbl_member where status=1 and name = ? and email = ?";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, paraMap.get("name"));
			pstmt.setString(2, aes.encrypt(paraMap.get("email"))); //db에 암호화되어 저장되어 있으므로 검색할때도 암호화해서 물어봐야 일치하는 거 찾아오기 가능

			rs = pstmt.executeQuery();
			
			if(rs.next()) { // select 해서 나온 값이 있다면
				userid = rs.getString(1); // select된 행의 1번째 컬럼의 값을 가져옴.
			}
			
		} catch (GeneralSecurityException | UnsupportedEncodingException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		
		return userid;
	}
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	// 비밀번호 찾기 (성명, 이메일을 입력받아서 해당 사용자가 존재하는지 유무를 알려준다.)
	@Override
	public boolean isUserExist(Map<String, String> paraMap) throws SQLException {
		
		boolean isUserExist = false;
		
		try {

			conn = ds.getConnection();

			String sql = "select userid from tbl_member where status = 1 and userid = ? and email = ?";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, paraMap.get("userid"));
			pstmt.setString(2, aes.encrypt(paraMap.get("email"))); //db에 암호화되어 저장되어 있으므로 검색할때도 암호화해서 물어봐야 일치하는 거 찾아오기 가능

			rs = pstmt.executeQuery();
			
			isUserExist = rs.next();
			
		} catch (GeneralSecurityException | UnsupportedEncodingException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return isUserExist;
	}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	//비밀번호 변경하기
	@Override
	public int pwdUpdate(Map<String, String> paraMap) throws SQLException {

		int result = 0;
		
		try {

			conn = ds.getConnection();

			String sql = "update tbl_member set pwd = ? where userid = ?";

			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, Sha256.encrypt(paraMap.get("pwd")));
			pstmt.setString(2, paraMap.get("userid"));

			result = pstmt.executeUpdate();

		} finally {
			close();
		}

		return result;
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	
	@Override
	public int coinUpdate(Map<String, String> paraMap) throws SQLException {

		int result = 0;
		
		try {

			conn = ds.getConnection();

			String sql = "update tbl_member set coin = coin+?, point = point+? where userid = ? ";

			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, paraMap.get("coinmoney"));
			pstmt.setInt(2, (int)(Integer.parseInt(paraMap.get("coinmoney"))*0.01)); //paraMap에 담긴 coinmoney키의 value값은 String타입이므로 int타입으로 형변환, 0.01은 더블타입이므로 계산후 타입도 double이므로 int로 형변환해줌			
			pstmt.setString(3, paraMap.get("userid"));

			result = pstmt.executeUpdate();
			
			System.out.println(result);
		} finally {
			close();
		}
		
		return result;
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public int updateMember(MemberVO member) throws SQLException {
		
		int result = 0;

		try {

			conn = ds.getConnection();

			String sql = "update tbl_member set name = ?, pwd = ?, email = ?, mobile = ?, postcode = ?, address = ?, detailaddress = ?, extraaddress = ? "
					+ "where userid = ?";

			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, member.getName());
			pstmt.setString(2, Sha256.encrypt(member.getPwd()));
			pstmt.setString(3, aes.encrypt(member.getEmail()));
			pstmt.setString(4, aes.encrypt(member.getMobile()));
			pstmt.setString(5, member.getPostcode());
			pstmt.setString(6, member.getAddress());
			pstmt.setString(7, member.getDetailaddress());
			pstmt.setString(8, member.getExtraaddress());
			pstmt.setString(9, member.getUserid());

			result = pstmt.executeUpdate();

		} catch (GeneralSecurityException | UnsupportedEncodingException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		
		return result;
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	// *** 페이징 처리를 하지않고 모든 회원목록 보여주기 *** //
	@Override
	public List<MemberVO> selectMember(Map<String, String> paraMap) throws SQLException {

		List<MemberVO> memberList = new ArrayList<>();
		
		try {

			conn = ds.getConnection();

			String sql = "select userid, name, email, gender "
					+ "from tbl_member "
					+ "where userid != 'admin' ";
		
			
			String colname = paraMap.get("searchType");
			String searchWord = paraMap.get("searchWord");	
			
			if("email".equals(colname)) {
				// 검색대상이 email인 경우
				searchWord = aes.encrypt(searchWord); //암호화해주기 -> DB에 암호화된 상태로 저장돼 있으므로
			} // 검색대상이 email이 아닌 경우 searchWord를 암호화를 하지 않고 넘어감
			
			if(searchWord != null && !searchWord.trim().isEmpty() ) { //검색어를 입력하지 않았거나 공백이지 않을때
				sql += " and "+colname +" like '%'||?||'%' "; //테이블명과 컬럼명에는 위치홀더 사용불가(변수 사용가능), 데이터만 위치홀더 사용가능 
			}
				sql += " order by registerday desc "; // 최근 가입자 먼저 보여주려고 desc

			pstmt = conn.prepareStatement(sql);

			if(searchWord != null && !searchWord.trim().isEmpty() ) { //검색어를 입력하지 않았거나 공백이지 않을때
				pstmt.setString(1, searchWord);	//첫번째 위치홀더에 검색어를 넣어준다.
			}
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) { //행이 여러개나오니까 다음행이 없을때까지 반복
				
				MemberVO mvo = new MemberVO();
				mvo.setUserid(rs.getString(1)); // memberVO에 읽어온 행의 userid를 넣어줌
				mvo.setName(rs.getString(2));
				mvo.setEmail(aes.decrypt(rs.getString(3))); //이메일은 암호화되어 있으므로 복호화하여 memberVO에 넣어줌
				mvo.setGender(rs.getString(4));
				
				memberList.add(mvo); // 생성된 memberVO를 memberList에 담아준다.
				
			}
			
		} catch (GeneralSecurityException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}  finally {
			close();
		}
		
		return memberList;
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	
	@Override
	public List<MemberVO> selectPagingMember(Map<String, String> paraMap) throws SQLException {

		List<MemberVO> memberList = new ArrayList<>();
		
		try {

			conn = ds.getConnection();

			String sql = "select userid, name, email, gender "+
					"from  "+
					"( "+
					" select rownum as rno, userid, name, email, gender "+
					" from "+
					" ( "+
					" select userid, name, email, gender "+
					" from tbl_member "+
					" where userid != 'admin' ";
			
			String colname = paraMap.get("searchType");
			String searchWord = paraMap.get("searchWord");	
			
			if("email".equals(colname)) {
				// 검색대상이 email인 경우
				searchWord = aes.encrypt(searchWord); //암호화해주기 -> DB에 암호화된 상태로 저장돼 있으므로
			} // 검색대상이 email이 아닌 경우 searchWord를 암호화를 하지 않고 넘어감
			
			if(searchWord != null && !searchWord.trim().isEmpty() ) { //검색어를 입력하지 않았거나 공백이지 않을때
				sql += " and "+colname+" like '%' || ? || '%' "; //테이블명과 컬럼명에는 위치홀더 사용불가(변수 사용가능), 데이터만 위치홀더 사용가능 
			}
				sql += " order by registerday desc "+ 	// 최근 가입자 먼저 보여주려고 desc
						" ) V "+
						" ) T "+
						"where rno between ? and ?"; 	// 페이지 보여주기 위한 조건

			pstmt = conn.prepareStatement(sql);

			int currentShowPageNo = Integer.parseInt(paraMap.get("currentShowPageNo"));
			int sizePerPage = Integer.parseInt(paraMap.get("sizePerPage"));
			
			if(searchWord != null && !searchWord.trim().isEmpty() ) { //검색어를 입력하지 않았거나 공백이지 않을때
				
				pstmt.setString(1, searchWord);	//첫번째 위치홀더에 검색어를 넣어준다.
				pstmt.setInt(2, ( currentShowPageNo * sizePerPage) - (sizePerPage - 1)); // 공식
				pstmt.setInt(3,  ( currentShowPageNo * sizePerPage) );					// 공식
			}
			else { // 검색어가 있는 경우
				
				pstmt.setInt(1, ( currentShowPageNo * sizePerPage) - (sizePerPage - 1)); // 공식
				pstmt.setInt(2,  ( currentShowPageNo * sizePerPage) );					// 공식
			}
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) { //행이 여러개나오니까 다음행이 없을때까지 반복
				
				MemberVO mvo = new MemberVO();
				mvo.setUserid(rs.getString(1)); // memberVO에 읽어온 행의 userid를 넣어줌
				mvo.setName(rs.getString(2));
				mvo.setEmail(aes.decrypt(rs.getString(3))); //이메일은 암호화되어 있으므로 복호화하여 memberVO에 넣어줌
				mvo.setGender(rs.getString(4));
				
				memberList.add(mvo); // 생성된 memberVO를 memberList에 담아준다.
				
			}
			
		} catch (GeneralSecurityException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}  finally {
			close();
		}
		
		return memberList;
	}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	
	//페이징처리를 위해 전체회원에 대한 총 페이지개수 알아오기(select)
	@Override
	public int getTotalPage(Map<String, String> paraMap) throws SQLException {

		int totalPage = 0;
		
		try {

			conn = ds.getConnection();

			String sql = " select ceil( count(*)/?) "
					+ " from tbl_member "
					+ " where userid != 'admin' ";
			
			String colname = paraMap.get("searchType");
			String searchWord = paraMap.get("searchWord");	
			
			if("email".equals(colname)) {
				// 검색대상이 email인 경우
				searchWord = aes.encrypt(searchWord); //암호화해주기 -> DB에 암호화된 상태로 저장돼 있으므로
			} // 검색대상이 email이 아닌 경우 searchWord를 암호화를 하지 않고 넘어감
			
			if(searchWord != null && !searchWord.trim().isEmpty() ) { //검색어를 입력하지 않았거나 공백이지 않을때
				sql += " and "+colname+" like '%' || ? || '%' "; //테이블명과 컬럼명에는 위치홀더 사용불가(변수 사용가능), 데이터만 위치홀더 사용가능 
			}
				
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, paraMap.get("sizePerPage"));
			
			if(searchWord != null && !searchWord.trim().isEmpty() ) { //검색어를 입력하지 않았거나 공백이지 않을때
				pstmt.setString(2, searchWord);	//두번째 위치홀더에 검색어를 넣어준다.
			}
			
			rs = pstmt.executeQuery();
			
			rs.next(); //커서를 내린다. (첫행으로~ 선택~)
			
			totalPage = rs.getInt(1); // select된 결과의 첫번째 컬럼 ceil( count(*)/10) 의 값을 받아온다.
			
		} catch (GeneralSecurityException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}  finally {
			close();
		}
		
		return totalPage;
	}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	
	// userid 값을 입력받아서 회원 1명에 대한 상세정보를 알아오기(select)
	@Override
	public MemberVO memberOneDetail(String userid) throws SQLException {

		MemberVO mvo = null;
		
		try {

			conn = ds.getConnection();

			String sql = "select userid, name, email, mobile, postcode, address, detailaddress, extraaddress, gender" + 
					" , substr(birthday,1,4) AS birthyyyy, substr(birthday,6,2) as birthmm, substr(birthday,9) as birthdd " + 
					" , coin, point, to_char(registerday, 'yyyy-mm-dd') as registerday " + 
					"from tbl_member "
					+ "where userid = ?";
		
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userid);
		
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				
				mvo = new MemberVO();
				mvo.setUserid(rs.getString(1));
				mvo.setName(rs.getString(2));
				mvo.setEmail(aes.decrypt(rs.getString(3))); // 복호화
				mvo.setMobile(aes.decrypt(rs.getString(4))); // 복호화
				mvo.setPostcode(rs.getString(5));
				mvo.setAddress(rs.getString(6));
				mvo.setDetailaddress(rs.getString(7));
				mvo.setExtraaddress(rs.getString(8));
				mvo.setGender(rs.getString(9));
				mvo.setBirthday(rs.getString(10) + rs.getString(11) + rs.getString(12));
				mvo.setCoin(rs.getInt(13));
				mvo.setPoint(rs.getInt(14));
				mvo.setRegisterday(rs.getString(15));
			
			}
			
		} catch (GeneralSecurityException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}  finally {
			close();
		}
		
		return mvo;
	}

}
