package kr.or.koroad.auth.filter;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import kr.or.koroad.auth.authentication.OtpAuthenticationToken;
import kr.or.koroad.auth.service.AbstractKoroadUserDetails;

public class OtpAuthenticationFilter extends AbstractAuthenticationProcessingFilter{

	protected OtpAuthenticationFilter() {
		super(new AntPathRequestMatcher("/auth/otp", "POST"));
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
		
		// 1. 현재 SecurityContext에서 1차 인증된 사용자 정보(username) 가져오기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if (authentication == null || //!authentication.isAuthenticated() ||
				!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_2FA_PENDING"))) {
			throw new BadCredentialsException("No pre-authentication found or missing ROLE_2FA_PENDING role");
		}
		
		AbstractKoroadUserDetails userDetails = (AbstractKoroadUserDetails)authentication.getPrincipal();
		String username = userDetails.getUsername();
		
		// 2. 요청 파라미터에서 OTP 코드 가져오기
		String otpCode = request.getParameter("otpCode");
		
		System.out.println("username :: " + username);
		System.out.println("otpCode :: " + otpCode);
		
		// 3. OtpAuthenticationToken 생성
        OtpAuthenticationToken authenticationToken = new OtpAuthenticationToken(userDetails, otpCode);

        // 4. AuthenticationManager에게 인증 위임
        // (AuthenticationManager는 OtpAuthenticationProvider를 찾아 실행)
        return this.getAuthenticationManager().authenticate(authenticationToken);
	}

}
