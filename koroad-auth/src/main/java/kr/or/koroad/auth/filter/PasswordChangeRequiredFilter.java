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
 * 비밀번호 변경이 필요한 사용자의 접근을 제한하는 필터
 * 
 * ROLE_PASSWORD_CHANGE_REQUIRED 권한을 가진 사용자는 
 * 비밀번호 변경 관련 URL만 접근 가능하도록 제한합니다.
 */
public class PasswordChangeRequiredFilter extends OncePerRequestFilter {

	private String changePasswordUrl = "/auth/change-password";
	private String changePasswordProcessUrl = "/auth/change-password/process";
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
		
		// ROLE_PASSWORD_CHANGE_REQUIRED 권한 확인
		boolean isPasswordChangeRequired = authentication.getAuthorities().stream()
				.anyMatch(auth -> auth.getAuthority().equals("ROLE_PASSWORD_CHANGE_REQUIRED"));
		
		if (isPasswordChangeRequired) {
			String requestURI = request.getRequestURI();
			String contextPath = request.getContextPath();
			
			// Context Path 제거
			String path = requestURI.startsWith(contextPath) 
					? requestURI.substring(contextPath.length()) 
					: requestURI;
			
			// 비밀번호 변경 관련 URL과 정적 리소스는 허용
			if (isAllowedUrl(path)) {
				System.out.println("=== 비밀번호 변경 필요 - 허용된 URL 접근 ===");
				System.out.println("요청 URL: " + path);
				filterChain.doFilter(request, response);
				return;
			}
			
			// 그 외의 URL 접근 시도 차단
			System.out.println("=== 비밀번호 변경 필요 - 접근 차단 ===");
			System.out.println("차단된 URL: " + path);
			System.out.println("비밀번호 변경 페이지로 리다이렉트");
			
			response.sendRedirect(contextPath + changePasswordUrl);
			return;
		}
		
		// ROLE_PASSWORD_CHANGE_REQUIRED가 없으면 정상 진행
		filterChain.doFilter(request, response);
	}
	
	/**
	 * 비밀번호 변경 필요 상태에서 허용되는 URL 확인
	 */
	private boolean isAllowedUrl(String path) {
		// 비밀번호 변경 관련 URL
		if (path.equals(changePasswordUrl) 
				|| path.equals(changePasswordProcessUrl)
				|| path.startsWith(changePasswordProcessUrl)
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
	public void setChangePasswordUrl(String changePasswordUrl) {
		this.changePasswordUrl = changePasswordUrl;
	}
	
	public void setChangePasswordProcessUrl(String changePasswordProcessUrl) {
		this.changePasswordProcessUrl = changePasswordProcessUrl;
	}
	
	public void setLogoutUrl(String logoutUrl) {
		this.logoutUrl = logoutUrl;
	}
}

