package kr.or.koroad.auth.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

public class KoroadAuthenticationFailureHandler implements AuthenticationFailureHandler{

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		System.out.println("인증 실패 exception : " + exception);
		
		// TODO 비밀번호 5회 실패시 Lock설정 (ESB호출)
		
        // 실패 이유에 따라 다른 메시지 처리 가능
		if (exception instanceof AuthenticationServiceException) {
			request.setAttribute("errorMessage", "사용자명 또는 비밀번호가 올바르지 않습니다.1");
		
		} else if(exception instanceof BadCredentialsException) {
			request.setAttribute("errorMessage", "사용자명 또는 비밀번호가 올바르지 않습니다.2");
			
		} else if(exception instanceof LockedException) {
			request.setAttribute("errorMessage", "잠긴 계정입니다..");
			
		} else if(exception instanceof DisabledException) {
			request.setAttribute("errorMessage", "비활성화된 계정입니다..");
			
		} else if(exception instanceof AccountExpiredException) {
			request.setAttribute("errorMessage", "만료된 계정입니다..");
			
		} else if(exception instanceof CredentialsExpiredException) {
			request.setAttribute("errorMessage", "비밀번호가 만료되었습니다.");
		} else {
			request.setAttribute("errorMessage", "인증에 실패했습니다.");
		}
		
		// error 파라미터 명시적으로 설정
		request.setAttribute("error", "true");
		
		// 로그인 페이지로 다시 포워딩
        request.getRequestDispatcher("/auth/failure").forward(request, response);
	}

}
