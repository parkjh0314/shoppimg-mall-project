package member.controller;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class MySMTPAuthenticator extends Authenticator {

   @Override
   public PasswordAuthentication getPasswordAuthentication() {
      
      // Gmail의 경우 @gmail.com을 제외한 아이디만 입력한다.
      return new PasswordAuthentication("nosenada9846", "qwer1234$");
   }
   
}