package kr.or.koroad.auth.security.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import kr.or.koroad.auth.service.AbstractKoroadUserDetails;

public class OtpAuthenticationFailureHandler implements AuthenticationFailureHandler{

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		
		HttpSession session = request.getSession(false);
		AbstractKoroadUserDetails userDetails = 
		       (AbstractKoroadUserDetails) session.getAttribute("SIGNIN_USER_DETAILS");
        
        request.setAttribute("username", userDetails.getUsername());
        
		// TODO 등록되지 않은 OTP 에 대한 처리가 필요
		System.out.println("OTP 인증 실패 exception : " + exception);
		
		
        // 실패 이유에 따라 다른 메시지 처리 가능
		if(exception instanceof BadCredentialsException) {
			request.setAttribute("message", "OTP번호가 올바르지 않습니다.");
			
		} else {
			request.setAttribute("message", "OTP인증에 실패했습니다.");
		}
		
		// error 파라미터 명시적으로 설정
		request.setAttribute("error", true);
		
		// 로그인 페이지로 다시 포워딩
        request.getRequestDispatcher("/auth/otp/failure").forward(request, response);
	}

}
