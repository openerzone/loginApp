package kr.or.koroad.auth.security.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

public class SigninAuthenticationFailureHandler implements AuthenticationFailureHandler{

	private UserDetailsService userDetailsService;
	
	private String changePasswordUrl = "/auth/change-password";
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		System.out.println("SignIn 인증 실패 exception : " + exception);
		
		
        // 실패 이유에 따라 다른 메시지 처리 가능
		if(exception instanceof BadCredentialsException) {
			request.setAttribute("message", "사용자명 또는 비밀번호가 올바르지 않습니다.");
			// TODO 비밀번호 5회 실패시 Lock설정 (ESB호출)
			
		} else if(exception instanceof LockedException) {
			request.setAttribute("message", "잠긴 계정입니다.");
			
		} else if(exception instanceof DisabledException) {
			request.setAttribute("message", "비활성화된 계정입니다.");
			
		} else if(exception instanceof AccountExpiredException) {
			request.setAttribute("message", "만료된 계정입니다.");
			
		} else if(exception instanceof CredentialsExpiredException) {
			// 비밀번호 만료 시 특별 처리
			System.out.println("=== 비밀번호 만료 감지 (FailureHandler) ===");
			handleCredentialsExpired(request, response);
			return;
			
		} else {
			request.setAttribute("message", "인증에 실패했습니다.");
		}
		
		// error 파라미터 명시적으로 설정
		request.setAttribute("error", true);
		
		// 로그인 페이지로 다시 포워딩
        request.getRequestDispatcher("/auth/failure").forward(request, response);
	}
	
	/**
	 * 비밀번호 만료 처리
	 * 임시 인증을 생성하여 비밀번호 변경 페이지로 리다이렉트
	 */
	private void handleCredentialsExpired(HttpServletRequest request, HttpServletResponse response) 
			throws IOException, ServletException {
		
		// 로그인 시도한 사용자명 가져오기
		String username = request.getParameter("username");
		
		if (username == null || username.trim().isEmpty()) {
			request.setAttribute("error", true);
			request.setAttribute("message", "사용자명을 확인할 수 없습니다.");
			request.getRequestDispatcher("/auth/failure").forward(request, response);
			return;
		}
		
		try {
			// UserDetailsService를 통해 사용자 정보 로드
			UserDetails userDetails = userDetailsService.loadUserByUsername(username);
			
			// ROLE_PASSWORD_CHANGE_REQUIRED 권한만 가진 임시 인증 생성
			List<GrantedAuthority> authorities = new ArrayList<>();
			authorities.add(new SimpleGrantedAuthority("ROLE_PASSWORD_CHANGE_REQUIRED"));
			
			Authentication tempAuth = new UsernamePasswordAuthenticationToken(
					userDetails,
					null,
					authorities
			);
			
			// SecurityContext에 임시 인증 저장
			SecurityContextHolder.getContext().setAuthentication(tempAuth);
			
			// 세션에도 저장 (세션 유지)
			HttpSession session = request.getSession(true);
			session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
			
			System.out.println("=== 비밀번호 만료 - 임시 인증 생성 ===");
			System.out.println("사용자: " + username);
			System.out.println("권한: ROLE_PASSWORD_CHANGE_REQUIRED");
			System.out.println("비밀번호 변경 페이지로 리다이렉트");
			
			// 비밀번호 변경 페이지로 리다이렉트
			response.sendRedirect(request.getContextPath() + changePasswordUrl);
			
		} catch (Exception e) {
			System.err.println("비밀번호 만료 처리 중 오류: " + e.getMessage());
			e.printStackTrace();
			
			request.setAttribute("error", true);
			request.setAttribute("message", "비밀번호가 만료되었습니다. 관리자에게 문의하세요.");
			request.getRequestDispatcher("/auth/failure").forward(request, response);
		}
	}

	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}
	
}
