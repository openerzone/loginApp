package kr.or.koroad.auth.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import kr.or.koroad.auth.service.AbstractKoroadUserDetails;

/**
 * 로그인 화면에 OTP 입력이 있다면 해당 provider를 등록한다.
 * @author opene
 *
 */
public class KoroadOtpAuthenticationProvider implements AuthenticationProvider {

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();
        String otpCode = (String) authentication.getPrincipal();
        
        // 인증된 사용자 정보 가져오기
     	AbstractKoroadUserDetails userDetails = (AbstractKoroadUserDetails) authentication.getPrincipal();
     	if (userDetails.isOtpEnabled()) {
     		// TODO OTP 검증
     	}
     		
        // otpCode 검증 
		return authentication;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		// TODO Auto-generated method stub
		return false;
	}

}
