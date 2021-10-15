<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<jsp:include page="../header.jsp" />  

<style>
   
   div#title {
      font-size: 20pt;
    /* border: solid 1px red; */
      padding: 12px 0;
   }
   
   div.mycontent {
        width: 300px;
        padding: 5px 3px;
     }
     
     div.mycontent>.title {
        font-size: 12pt;
        font-weight: bold;
        background-color: #d95050;
        color: #fff;
     }
     
     div.mycontent>.title>a {
        text-decoration: none;
        color: #fff;
     }
     
     
     div.mycontent>.desc {
      /* border: solid 1px red; */
        padding: 10px 0 0 0;
        color: #000;
        font-weight: normal;
        font-size: 9pt;
     }
     
     div.mycontent>.desc>img {
        width: 50px;
        height: 50px;
     }
 
</style>

 <div id="title">매장지도</div>
 <div id="map" style="width:90%; height:600px;"></div>
 <div id="latlngResult"></div>
 
 <%-- 
   <script type="text/javascript" src="//dapi.kakao.com/v2/maps/sdk.js?appkey=발급받은 APP KEY(JavaScript 키)를 넣으시면 됩니다."></script> 
 --%> 
 <script type="text/javascript" src="//dapi.kakao.com/v2/maps/sdk.js?appkey=14951dabfeee689f10690d1cca733719"></script> 
 
 <script type="text/javascript">
    $(document).ready(function(){
       
       // 지도를 담을 영역의 DOM 레퍼런스 
       var mapContainer = document.getElementById('map');
       
       // 지도를 생성할때 필요한 기본 옵션
       var options = {
              center: new kakao.maps.LatLng(37.56602747782394, 126.98265938959321), // 지도의 중심좌표. 반드시 존재해야함.
           level: 7 // 지도의 레벨(확대, 축소 정도). 숫자가 적을수록 확대된다. 4가 적당함.
       };
       /*
           center 에 할당할 값은 kakao.maps.LatLng 클래스를 사용하여 생성한다.
           kakao.maps.LatLng 클래스의 2개 인자값은 첫번째 파라미터는 위도(latitude)이고, 두번째 파라미터는 경도(longitude)이다.
         */
         
       // 지도 생성 및 생성된 지도객체 리턴
       var mapobj = new kakao.maps.Map(mapContainer, options);
       
       // 일반 지도와 스카이뷰로 지도 타입을 전환할 수 있는 지도타입 컨트롤을 생성함.    
       var mapTypeControl = new kakao.maps.MapTypeControl();
       
       // 지도 타입 컨트롤을 지도에 표시함.
       // kakao.maps.ControlPosition은 컨트롤이 표시될 위치를 정의하는데 TOPRIGHT는 오른쪽 위를 의미함.   
       mapobj.addControl(mapTypeControl, kakao.maps.ControlPosition.TOPRIGHT);
       
       // 지도 확대 축소를 제어할 수 있는  줌 컨트롤을 생성함.   
       var zoomControl = new kakao.maps.ZoomControl();
       
       // 지도 확대 축소를 제어할 수 있는  줌 컨트롤을 지도에 표시함.
       // kakao.maps.ControlPosition은 컨트롤이 표시될 위치를 정의하는데 RIGHT는 오른쪽을 의미함.    
       mapobj.addControl(zoomControl, kakao.maps.ControlPosition.RIGHT);
       
       if(navigator.geolocation) {
          // HTML5의 geolocation 으로 사용할 수 있는지 확인한다.
          
          // GeoLocation을 이용해서 웹페이지에 접속한 사용자의 현재 위치를 확인하여 그 위치(위도,경도)를 지도의 중앙에 오도록 한다.
          navigator.geolocation.getCurrentPosition(function(position) { 
             var latitude = position.coords.latitude;   // 현위치의 위도
             var longitude = position.coords.longitude; // 현위치의 경도
             
           // console.log("현위치의 위도: "+latitude+", 현위치의 경도: "+longitude);
          
           // 마커가 표시될 위치를 geolocation으로 얻어온 현위치의 위.경도 좌표로 한다   
             var locPosition = new kakao.maps.LatLng(latitude, longitude);
           
          // 마커이미지를 기본이미지를 사용하지 않고 다른 이미지로 사용할 경우의 이미지 주소 
               var imageSrc = 'http://localhost:9090/MyMVC/images/pointerPink.png';
          
            // 마커이미지의 크기 
              var imageSize = new kakao.maps.Size(34, 39);
               
           // 마커이미지의 옵션. 마커의 좌표와 일치시킬 이미지 안에서의 좌표를 설정한다. 
              var imageOption = {offset: new kakao.maps.Point(15, 39)};

           // 마커의 이미지정보를 가지고 있는 마커이미지를 생성한다. 
              var markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize, imageOption);
              
           // == 마커 생성하기 == //
             var marker = new kakao.maps.Marker({ 
                map: mapobj, 
                  position: locPosition, // locPosition 좌표에 마커를 생성 
                  image: markerImage     // 마커이미지 설정
            }); 
             
            marker.setMap(mapobj); // 지도에 마커를 표시한다
           
         // === 인포윈도우(텍스트를 올릴 수 있는 말풍선 모양의 이미지) 생성하기 === //
            
         // 인포윈도우에 표출될 내용으로 HTML 문자열이나 document element가 가능함.
         var iwContent = "<div style='padding:5px; font-size:9pt;'>여기에 계신가요?<br/><a href='https://map.kakao.com/link/map/현위치(약간틀림),"+latitude+","+longitude+"' style='color:blue;' target='_blank'>큰지도</a> <a href='https://map.kakao.com/link/to/현위치(약간틀림),"+latitude+","+longitude+"' style='color:blue' target='_blank'>길찾기</a></div>";
            
         // == 지도의 센터위치를 locPosition로 변경한다.(사이트에 접속한 클라이언트 컴퓨터의 현재의 위.경도로 변경한다.)
             mapobj.setCenter(locPosition);
             
          });
          
       }
       else {
          
       }
       
       
    });
 </script>  

<jsp:include page="../footer.jsp" />  
