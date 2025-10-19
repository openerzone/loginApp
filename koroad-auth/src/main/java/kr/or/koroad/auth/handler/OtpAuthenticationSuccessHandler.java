package kr.or.koroad.auth.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import kr.or.koroad.auth.service.AbstractKoroadUserDetails;
import kr.or.koroad.auth.service.OtpService;

/**
 * 로그인 성공 후 OTP 인증 화면으로 리다이렉트하는 핸들러
 */
public class OtpAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	@Autowired
	private OtpService otpService;
	
	// OTP 인증 완료 후 실행될 최종 핸들러 (외부에서 주입 가능)
	private AuthenticationSuccessHandler finalSuccessHandler;
	
	private String otpPageUrl = "/auth/otp"; // OTP 입력 페이지 URL
	
	/**
	 * 기본 생성자: SavedRequestAwareAuthenticationSuccessHandler를 최종 핸들러로 사용
	 */
	public OtpAuthenticationSuccessHandler() {
		this.finalSuccessHandler = new SavedRequestAwareAuthenticationSuccessHandler();
	}
	
	/**
	 * 커스텀 최종 핸들러를 주입받는 생성자
	 * @param finalSuccessHandler OTP 인증 완료 후 실행될 핸들러
	 */
	public OtpAuthenticationSuccessHandler(AuthenticationSuccessHandler finalSuccessHandler) {
		this.finalSuccessHandler = finalSuccessHandler != null 
				? finalSuccessHandler 
				: new SavedRequestAwareAuthenticationSuccessHandler();
	}
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
		// 인증된 사용자 정보 가져오기
		AbstractKoroadUserDetails userDetails = (AbstractKoroadUserDetails) authentication.getPrincipal();

		boolean isOptEnabled = userDetails.isOtpEnabled();
		
		if (isOptEnabled) {
			// 1. 기존 권한 가져오기
			Collection<? extends GrantedAuthority> existingAuthorities = authentication.getAuthorities();
			
			// 2. 새로운 권한 리스트 생성 (기존 권한 + ROLE_2FA_PENDING)
			List<GrantedAuthority> newAuthorities = new ArrayList<>(existingAuthorities);
			newAuthorities.add(new SimpleGrantedAuthority("ROLE_2FA_PENDING"));
            
            // 3. 기존 권한 + ROLE_2FA_PENDING을 가진 새로운 Authentication 객체 생성
            Authentication partialAuth = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null, // 비밀번호는 더 이상 필요 없으므로 null 처리
                    newAuthorities // 기존 권한 + ROLE_2FA_PENDING
            );
            
            System.out.println("=== OTP 인증 대기 상태로 전환 ===");
            System.out.println("사용자: " + userDetails.getUsername());
            System.out.println("기존 권한: " + existingAuthorities);
            System.out.println("새 권한: " + newAuthorities);
            
            // 4. SecurityContextHolder에 업데이트된 인증 정보 저장
            SecurityContextHolder.getContext().setAuthentication(partialAuth);
            
            // 5. OTP 인증 페이지로 리디렉션
			response.sendRedirect(request.getContextPath() + otpPageUrl);
		} else {
			// OTP가 비활성화된 경우, 최종 핸들러로 위임 (즉시 로그인 완료)
			System.out.println("=== OTP 비활성화 - 최종 핸들러로 위임 ===");
            this.finalSuccessHandler.onAuthenticationSuccess(request, response, authentication);
		}
		/*
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
		*/
	}
	
	public void setOtpPageUrl(String otpPageUrl) {
		this.otpPageUrl = otpPageUrl;
	}
	
	/**
	 * OTP 인증 완료 후 실행될 최종 핸들러 설정
	 * @param finalSuccessHandler 최종 성공 핸들러
	 */
	public void setFinalSuccessHandler(AuthenticationSuccessHandler finalSuccessHandler) {
		this.finalSuccessHandler = finalSuccessHandler != null 
				? finalSuccessHandler 
				: new SavedRequestAwareAuthenticationSuccessHandler();
	}
	
	/**
	 * 최종 핸들러 반환 (OTP 검증 후 사용)
	 * @return 최종 성공 핸들러
	 */
	public AuthenticationSuccessHandler getFinalSuccessHandler() {
		return this.finalSuccessHandler;
	}
}

