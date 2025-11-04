package kr.or.koroad.auth.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import kr.or.koroad.auth.authentication.SsoAuthenticationToken;
import kr.or.koroad.auth.service.AbstractKoroadUserDetails;

public class KoroadSsoAuthenticationProvider implements AuthenticationProvider {

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		// 인증된 사용자 정보 가져오기
		AbstractKoroadUserDetails userDetails = (AbstractKoroadUserDetails) authentication.getPrincipal();
		
		if (!userDetails.isSsoEnabled())
			return authentication;
		
		String username = userDetails.getUsername();
		System.out.println("----------------------------------");
		System.out.println("-------KoroadSsoAuthenticationProvider--------" + username);
		
		System.out.println("----------------------------------");
		
//		TODO SSO 인증
		return authentication;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return (SsoAuthenticationToken.class.isAssignableFrom(authentication));
	}

}
