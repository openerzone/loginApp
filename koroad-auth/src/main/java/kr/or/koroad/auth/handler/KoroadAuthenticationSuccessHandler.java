package kr.or.koroad.auth.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * 로그인 성공 시 세션에 사용자 정보를 저장하는 핸들러
 * UserDetailsService와 연동하여 로그인 성공 시 자동으로 세션에 사용자 정보 저장
 * TODO. abstract 로 만들고 각 응용단에서 재정의
 */
@Component(value = "koroadAuthenticationSuccessHandler")
public class KoroadAuthenticationSuccessHandler implements AuthenticationSuccessHandler{

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		// 로그인 성공 후 메인 페이지로 리다이렉트
        response.sendRedirect("/cmm/main/mainPage.do");
	}

}
