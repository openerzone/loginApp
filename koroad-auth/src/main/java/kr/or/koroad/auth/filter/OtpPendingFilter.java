package kr.or.koroad.auth.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * OTP 인증 대기 중인 사용자의 접근을 제한하는 필터
 * 
 * ROLE_2FA_PENDING 권한을 가진 사용자는 OTP 관련 URL만 접근 가능하도록 제한합니다.
 */
public class OtpPendingFilter extends OncePerRequestFilter {

	private String otpPageUrl = "/auth/otp";
	private String otpVerifyUrl = "/auth/otp/verify";
	private String otpResendUrl = "/auth/otp/resend";
	private String logoutUrl = "/auth/logout";
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		// 인증되지 않았거나 익명 사용자면 다음 필터로
		if (authentication == null || !authentication.isAuthenticated() 
				|| "anonymousUser".equals(authentication.getPrincipal())) {
			filterChain.doFilter(request, response);
			return;
		}
		
		// ROLE_2FA_PENDING 권한 확인
		boolean isOtpPending = authentication.getAuthorities().stream()
				.anyMatch(auth -> auth.getAuthority().equals("ROLE_2FA_PENDING"));
		
		if (isOtpPending) {
			String requestURI = request.getRequestURI();
			String contextPath = request.getContextPath();
			
			// Context Path 제거
			String path = requestURI.startsWith(contextPath) 
					? requestURI.substring(contextPath.length()) 
					: requestURI;
			
			// OTP 관련 URL과 정적 리소스는 허용
			if (isAllowedUrl(path)) {
				System.out.println("=== OTP 대기 상태 - 허용된 URL 접근 ===");
				System.out.println("요청 URL: " + path);
				filterChain.doFilter(request, response);
				return;
			}
			
			// 그 외의 URL 접근 시도 차단
			System.out.println("=== OTP 대기 상태 - 접근 차단 ===");
			System.out.println("차단된 URL: " + path);
			System.out.println("OTP 인증 페이지로 리다이렉트");
			
			response.sendRedirect(contextPath + otpPageUrl);
			return;
		}
		
		// ROLE_2FA_PENDING이 없으면 정상 진행
		filterChain.doFilter(request, response);
	}
	
	/**
	 * OTP 대기 상태에서 허용되는 URL 확인
	 */
	private boolean isAllowedUrl(String path) {
		// OTP 관련 URL
		if (path.equals(otpPageUrl) 
				|| path.equals(otpVerifyUrl) 
				|| path.equals(otpResendUrl)
				|| path.equals(logoutUrl)) {
			return true;
		}
		
		// 정적 리소스 허용
		if (path.startsWith("/auth/static/") ) {
//				|| path.startsWith("/css/") 
//				|| path.startsWith("/js/") 
//				|| path.startsWith("/images/")
//				|| path.endsWith(".css")
//				|| path.endsWith(".js")
//				|| path.endsWith(".png")
//				|| path.endsWith(".jpg")
//				|| path.endsWith(".gif")
//				|| path.endsWith(".ico")
//				|| path.endsWith(".avif")) {
			return true;
		}
		
		return false;
	}
	
	// Setter methods for configuration
	public void setOtpPageUrl(String otpPageUrl) {
		this.otpPageUrl = otpPageUrl;
	}
	
	public void setOtpVerifyUrl(String otpVerifyUrl) {
		this.otpVerifyUrl = otpVerifyUrl;
	}
	
	public void setOtpResendUrl(String otpResendUrl) {
		this.otpResendUrl = otpResendUrl;
	}
	
	public void setLogoutUrl(String logoutUrl) {
		this.logoutUrl = logoutUrl;
	}
}

