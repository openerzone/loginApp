package kr.or.koroad.auth.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import kr.or.koroad.auth.service.AbstractKoroadUserDetails;

public class KoroadSsoAuthenticationProvider implements AuthenticationProvider {

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		// 인증된 사용자 정보 가져오기
//		AbstractKoroadUserDetails userDetails = (AbstractKoroadUserDetails) authentication.getPrincipal();
//		if (userDetails.isSSO()) {
//			
//			String username = userDetails.getUsername();
//			String password = userDetails.getPassword();
//			// TODO SSO 호출 (id, password)
//			// sso 인증여부
//			String sabun = authentication.getName();
//			// 무엇을 리턴하지
//			// return new UsernamePasswordAuthenticationToken(username, pwd, authorities);
//		}
		return authentication;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
	}

}
