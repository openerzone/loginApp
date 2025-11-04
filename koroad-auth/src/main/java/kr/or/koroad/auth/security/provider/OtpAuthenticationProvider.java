package kr.or.koroad.auth.security.provider;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import kr.or.koroad.auth.authentication.OtpAuthenticationToken;
import kr.or.koroad.auth.service.AbstractKoroadUserDetails;
import kr.or.koroad.auth.service.OtpService;

public class OtpAuthenticationProvider implements AuthenticationProvider {

	private OtpService otpService;
	
	/**
     * 실제 인증 로직
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    	
    	AbstractKoroadUserDetails userDetails = (AbstractKoroadUserDetails) authentication.getPrincipal();
        
    	if (!userDetails.isOtpEnabled()) {
            return authentication;	
    	}
    	
    	String username = userDetails.getUsername();
    	System.out.println("OTP provider username :: " + username);
        String otp = (String) authentication.getCredentials();

        // 1. 실제 OTP 서비스(Google Authenticator 등)를 통해 OTP 검증
//        if (!otpService.verifyOtp(username, otp)) {
    	if (!"123456".equals(otp)) {
            throw new BadCredentialsException("Invalid OTP code");
        }

//        userDetails.removeAuthority("ROLE_2FA_PENDING");
        
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null, // Credentials는 null
                userDetails.getAuthorities() // 최종 권한
        );
    }

    /**
     * 이 Provider가 어떤 타입의 Authentication을 처리할지 지정
     */
    @Override
    public boolean supports(Class<?> authentication) {
        // OtpAuthenticationToken 타입의 인증 요청을 처리
        return OtpAuthenticationToken.class.isAssignableFrom(authentication);
    }
	
	public void setOtpService(OtpService otpService) {
		this.otpService = otpService;
	}
}

