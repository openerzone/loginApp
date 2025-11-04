package kr.or.koroad.auth.security.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import kr.or.koroad.auth.service.AbstractKoroadUserDetails;

public class SigninAuthenticationSuccesshandler implements AuthenticationSuccessHandler{

	@Autowired
	private SsoAuthenticationSuccessHandler ssoAuthenticationSuccessHandler;
	
	private String otpPath = "/auth/otp";
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
		AbstractKoroadUserDetails userDetails = (AbstractKoroadUserDetails)authentication.getPrincipal();
		
		if (userDetails.isOtpEnabled() || 
				authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_2FA_PENDING"))) {
//	        // 1. 기존 권한 가져오기
//	        Collection<? extends GrantedAuthority> authorities = 
//	            authentication.getAuthorities();  // 현재: []
//	        
//	        // 2. 새로운 권한 리스트 생성
//	        List<GrantedAuthority> newAuthorities = new ArrayList<>(authorities);
//	        newAuthorities.add(new SimpleGrantedAuthority("ROLE_2FA_PENDING"));
//	        // 결과: [ROLE_2FA_PENDING]
//	        
//	        // 3. 새로운 Authentication 객체 생성 (중요!)
//	        Authentication _authentication = new UsernamePasswordAuthenticationToken(
//	            userDetails,
//	            null,
//	            newAuthorities  // ← 새로운 권한으로 생성
//	        );
//	        _authentication.setAuthenticated(false);
//	        
//	        // 4. 새로운 객체를 SecurityContext에 저장
//	        SecurityContextHolder.getContext().setAuthentication(_authentication);
	        HttpSession session = request.getSession();
	        session.setAttribute("SIGNIN_USER_DETAILS", userDetails);
	        
			response.sendRedirect(otpPath);
		} else {
			this.ssoAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);
		}
		
		
	}

//	public void setOtpPath(String otpPath) {
//		this.otpPath = otpPath;
//	}
}
