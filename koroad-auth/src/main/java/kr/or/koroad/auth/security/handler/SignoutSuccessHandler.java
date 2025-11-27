package kr.or.koroad.auth.security.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

public class SignoutSuccessHandler implements LogoutSuccessHandler {

	private LogoutSuccessHandler customLogoutHandler;
	
	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		
		System.out.println("-------------SignoutSuccessHandler---------------");
		System.out.println("-------------SignoutSuccessHandler:authentication---------------");
		System.out.println("authentication.isAuthenticated() ::: " + authentication.isAuthenticated());
		System.out.println("authentication ::: " + authentication);
		
		this.customLogoutHandler.onLogoutSuccess(request, response, authentication);
	}

	public void setCustomLogoutHandler(LogoutSuccessHandler customLogoutHandler) {
		this.customLogoutHandler = customLogoutHandler;
	}
}
