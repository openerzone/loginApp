package kr.or.koroad.auth.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import kr.or.koroad.auth.service.OtpService;

/**
 * 로그인 성공 후 OTP 인증 화면으로 리다이렉트하는 핸들러
 */
public class OtpAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	@Autowired
	private OtpService otpService;
	
	private String otpPageUrl = "/auth/otp"; // OTP 입력 페이지 URL
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
		// 인증된 사용자 정보 가져오기
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		String username = userDetails.getUsername();
		
		System.out.println("=== 로그인 성공 ===");
		System.out.println("사용자: " + username);
		System.out.println("OTP 생성 및 발송 시작");
		
		// OTP 생성
		String otpCode = otpService.generateOtp(username);
		
		// 세션에 OTP 인증 대기 상태 저장
		HttpSession session = request.getSession();
		session.setAttribute("OTP_PENDING_USER", username);
		session.setAttribute("OTP_AUTHENTICATED", false);
		
		// 테스트용: 세션에 OTP 코드 저장 (실제 운영에서는 제거)
		session.setAttribute("OTP_CODE_FOR_TESTING", otpCode);
		
		// OTP 입력 페이지로 리다이렉트
		System.out.println("OTP 입력 페이지로 리다이렉트: " + otpPageUrl);
		response.sendRedirect(request.getContextPath() + otpPageUrl);
	}
	
	public void setOtpPageUrl(String otpPageUrl) {
		this.otpPageUrl = otpPageUrl;
	}
}

