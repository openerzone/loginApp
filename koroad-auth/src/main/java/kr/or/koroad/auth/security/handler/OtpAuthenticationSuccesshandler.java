package kr.or.koroad.auth.security.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import kr.or.koroad.auth.service.AbstractKoroadUserDetails;

public class OtpAuthenticationSuccesshandler implements AuthenticationSuccessHandler{

	@Autowired
	private SsoAuthenticationSuccessHandler ssoAuthenticationSuccessHandler;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
		AbstractKoroadUserDetails userDetails = (AbstractKoroadUserDetails)authentication.getPrincipal();
		
		userDetails.removeAuthority("ROLE_2FA_PENDING");
		
		HttpSession session = request.getSession();
		session.removeAttribute("SIGNIN_USER_DETAILS");
		session.removeAttribute("OTP_AUTHENTICATED");
		session.removeAttribute("OTP_CODE_FOR_TESTING");
		
		System.out.println("-----------OTP 성공 : role 제거---------");
		
		this.ssoAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);
		
	}
}
