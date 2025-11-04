package kr.or.koroad.auth.security.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import kr.or.koroad.auth.service.AbstractKoroadUserDetails;

public class SsoAuthenticationSuccessHandler implements AuthenticationSuccessHandler{

	private AuthenticationSuccessHandler customSuccessHandler;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
		AbstractKoroadUserDetails userDetails = (AbstractKoroadUserDetails)authentication.getPrincipal();
		boolean ssoEnabled = userDetails.isSsoEnabled();
		
		if (ssoEnabled) {
			System.out.println("-----------SSO 호출--------------");
		}
		
		this.customSuccessHandler.onAuthenticationSuccess(request, response, authentication);
	}

	public void setCustomSuccessHandler(AuthenticationSuccessHandler customSuccessHandler) {
		this.customSuccessHandler = customSuccessHandler;
	}
	
}