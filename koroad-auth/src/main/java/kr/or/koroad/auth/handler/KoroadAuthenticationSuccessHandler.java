package kr.or.koroad.auth.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * 로그인 성공 시 세션에 사용자 정보를 저장하는 핸들러
 * UserDetailsService와 연동하여 로그인 성공 시 자동으로 세션에 사용자 정보 저장
 * TODO 응용단에서 필요시 자체구현
 */
public class KoroadAuthenticationSuccessHandler implements AuthenticationSuccessHandler{

	private String redirectPath;
	
	public void setRedirectPath(String redirectPath) {
		this.redirectPath = redirectPath;
	}
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		// 로그인 성공 후 메인 페이지로 리다이렉트
        response.sendRedirect(redirectPath);
	}

}
