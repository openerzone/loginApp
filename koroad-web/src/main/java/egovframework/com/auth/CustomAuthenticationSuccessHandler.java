package egovframework.com.auth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

/**
 * koroad-web 전용 커스텀 인증 성공 핸들러
 * OTP 인증 완료 후 실행됨
 */
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	@Value("${auth.success.redirect.path:/}")
	private String defaultTargetUrl;
	
	// 기본 핸들러를 Delegation으로 사용
	private final AuthenticationSuccessHandler defaultHandler = new SavedRequestAwareAuthenticationSuccessHandler();
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
		System.out.println("====================================");
		System.out.println("=== 커스텀 인증 성공 핸들러 실행 ===");
		System.out.println("====================================");
		System.out.println("사용자: " + authentication.getName());
		System.out.println("권한: " + authentication.getAuthorities());
		
		// 세션에 추가 정보 저장 예시
		HttpSession session = request.getSession();
		session.setAttribute("LAST_LOGIN_TIME", System.currentTimeMillis());
		session.setAttribute("LOGIN_IP", request.getRemoteAddr());
		
		// TODO: 여기에 커스텀 로직 추가
		// 예: 로그인 이력 DB 저장, 사용자별 설정 로드, 알림 발송 등
		
		System.out.println("커스텀 로직 실행 완료");
		System.out.println("기본 리다이렉트 처리: " + defaultTargetUrl);
		System.out.println("====================================");
		
		// 기본 핸들러로 위임 (SavedRequest 처리 + 리다이렉트)
		defaultHandler.onAuthenticationSuccess(request, response, authentication);
	}
}

